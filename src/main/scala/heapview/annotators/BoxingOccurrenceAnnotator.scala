package heapview.annotators

import heapview.ElementAnnotator
import org.jetbrains.plugins.scala.annotator.ScalaAnnotationHolder
import org.jetbrains.plugins.scala.extensions.PsiClassExt
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScExpression
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScExpression.ExpressionTypeResult
import org.jetbrains.plugins.scala.lang.psi.types.api.ValType

object BoxingOccurrenceAnnotator extends ElementAnnotator[ScExpression] {
  override def annotate(element: ScExpression, typeAware: Boolean)(implicit holder: ScalaAnnotationHolder): Unit =
    element match {
      case ExplicitConversion(msg) => msg.annotate(element, holder)
      case _                       =>
    }

  object ExplicitConversion {
    def unapply(expr: ScExpression): Option[BoxingAllocationMessage] = {
      val ExpressionTypeResult(actualType, _, _) = expr.getTypeAfterImplicitConversion()
      val ascribedType = expr.expectedType()

      (actualType.map(_.widenIfLiteral), ascribedType) match {
        case (Right(actual: ValType), Some(ascribed)) =>
          Option.when(
            ascribed.isAnyVal || ascribed.isAny || ascribed.extractClass.exists(_.qualifiedName == "java.lang.Object")
          )(BoxingAllocationMessage("explicit conversion", actual.toString(), "Object"))
        case _ => None
      }
    }
  }
}
