package heapview

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.scala.annotator.ScalaAnnotationHolder
import org.jetbrains.plugins.scala.highlighter.DefaultHighlighter

package object annotators {
  case class BoxingAllocationMessage(format: String, p1: String, p2: String) {
    def annotate(element: PsiElement, holder: ScalaAnnotationHolder) = {
      holder.createInfoAnnotation(
        element,
        s"Boxing allocation: $format from '$p1' to '$p2'",
        Some(DefaultHighlighter.MUTABLE_COLLECTION.getDefaultAttributes)
      )
    }
  }
}
