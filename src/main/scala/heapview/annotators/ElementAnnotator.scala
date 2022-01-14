package heapview.annotators

import org.jetbrains.plugins.scala.annotator.ScalaAnnotationHolder
import org.jetbrains.plugins.scala.lang.psi.api.ScalaPsiElement

abstract class ElementAnnotator[T: reflect.ClassTag] {

  def annotate(element: T, typeAware: Boolean)(implicit
      holder: ScalaAnnotationHolder
  ): Unit

  def doAnnotate(element: ScalaPsiElement, typeAware: Boolean)(implicit
      holder: ScalaAnnotationHolder
  ): Unit = element match {
    case element: T => annotate(element, typeAware)
    case _          =>
  }
}
object ElementAnnotator extends ElementAnnotator[ScalaPsiElement] {

  // we already have typeclasses at home :(
  private val Instances =
    HeapAllocationAnnotator :: Nil

  override def annotate(element: ScalaPsiElement, typeAware: Boolean)(implicit
      holder: ScalaAnnotationHolder
  ) =
    Instances.foreach {
      _.doAnnotate(element, typeAware)
    }

}
