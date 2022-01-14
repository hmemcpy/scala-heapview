package heapview.annotators

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.scala.annotator.ScalaAnnotationHolder
import org.jetbrains.plugins.scala.extensions.PsiClassExt
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScExpression.ExpressionTypeResult
import org.jetbrains.plugins.scala.lang.psi.api.expr.{ScExpression, ScMethodCall, ScReferenceExpression}
import org.jetbrains.plugins.scala.lang.psi.api.statements.ScFunctionDefinition
import org.jetbrains.plugins.scala.lang.psi.types.api.ValType
import org.jetbrains.plugins.scala.lang.psi.types.{ScAbstractType, ScType}

object HeapAllocationAnnotator extends ElementAnnotator[ScExpression] {
  override def annotate(element: ScExpression, typeAware: Boolean)(implicit holder: ScalaAnnotationHolder): Unit =
    element match {
      case ExplicitConversion(msg)     => msg.annotate(element, holder)
      case GenericMethodCall(msg, arg) => msg.annotate(arg, holder)
      case _                           =>
    }

  object ExplicitConversion {
    def unapply(expr: ScExpression): Option[HeapAllocationMessage] =
      expr match {
        case IsValueType(valueType, Some(expected)) =>
          Option.when(
            expected.isAnyVal || expected.isAny || expected.extractClass.exists(_.qualifiedName == "java.lang.Object")
          )(HeapAllocationMessage("Boxing", s"value type '$valueType' is converted to 'Object'"))
        case _ => None
      }
  }

  object GenericMethodCall {
    def unapply(expr: ScExpression): Option[(HeapAllocationMessage, PsiElement)] =
      expr match {
        case ScMethodCall(ref @ ScReferenceExpression(_), Seq(param)) =>
          ref.resolve() match {
            case fd: ScFunctionDefinition if fd.hasTypeParameters() =>
              fd.parameters match {
                // single argument method, the argument is vararg -> allocates an array
                case Seq(p) if p.isVarArgs() =>
                  val t = param match {
                    case IsValueType(vt, _) => s"[${vt.name}]"
                    case other =>
                      val ExpressionTypeResult(actualType, _, _) = other.getTypeAfterImplicitConversion()
                      actualType.map(_.widenIfLiteral).fold(_ => "", t => s"[$t]")
                  }
                  Some((HeapAllocationMessage("Heap", s"vararg method '${fd.name}' allocates an Array$t"), param))
                case _ =>
                  param match {
                    case IsValueType(valueType, Some(t: ScAbstractType)) =>
                      val name = t.typeParameter.name
                      Some((HeapAllocationMessage("Boxing", s"value type '$valueType' is passed to generic parameter '$name'"), param))
                    case _ => None
                  }
              }
            case _ => None
          }
        case _ => None
      }
  }

  object IsValueType {
    def unapply(expr: ScExpression): Option[(ValType, Option[ScType])] = {
      val ExpressionTypeResult(actualType, _, _) = expr.getTypeAfterImplicitConversion()
      val expected = expr.expectedType()

      actualType.map(_.widenIfLiteral) match {
        case Right(valType: ValType) => Some((valType, expected))
        case _                       => None
      }
    }
  }
}
