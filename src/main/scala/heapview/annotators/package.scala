package heapview

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.scala.annotator.ScalaAnnotationHolder
import org.jetbrains.plugins.scala.highlighter.DefaultHighlighter

package object annotators {
  case class HeapAllocationMessage(kind: String, message: String) {
    def annotate(element: PsiElement, holder: ScalaAnnotationHolder) = {
      holder.createInfoAnnotation(
        element,
        s"$kind allocation: $message",
        Some(DefaultHighlighter.MUTABLE_COLLECTION.getDefaultAttributes)
      )
    }
  }
}
