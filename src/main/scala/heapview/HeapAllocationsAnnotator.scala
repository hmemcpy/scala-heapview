package heapview

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.scala.annotator.annotationHolder.ScalaAnnotationHolderAdapter
import org.jetbrains.plugins.scala.lang.psi.api.ScalaPsiElement

class HeapAllocationsAnnotator extends Annotator {
  override def annotate(element: PsiElement, holder: AnnotationHolder): Unit = {
    val myHolder = new ScalaAnnotationHolderAdapter(holder)
    element match {
      case sc: ScalaPsiElement => ElementAnnotator.annotate(sc, true)(myHolder)
      case _                   =>
    }
  }
}
