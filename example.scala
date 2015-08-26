package example

import spray.json._

case class Foo(x: Int, y: Int)

case class Parent1(a: String, foo: Foo)

object FooFormat extends DefaultJsonProtocol {
 implicit val format: RootJsonFormat[Foo] = jsonFormat2(Foo)
}

object Parent1Format extends DefaultJsonProtocol {
 import FooFormat.format
 implicit val format2 = jsonFormat2(Parent1)
}

class CustomJsonFormat extends RootJsonFormat[Parent1] {
		def read(value: JsValue) = value.asJsObject.getFields("a","x", "y") match {
		      case Seq(JsString(status), JsNumber(x), JsNumber(y)) =>
		        Parent1(status, Foo(x.toInt, y.toInt))
		      case _ => deserializationError("a,x,y expected (in that order)")
		    }
		def write(obj: Parent1) = JsObject(
			"a" -> JsString(obj.a), "x" -> JsNumber(obj.foo.x), "y" -> JsNumber(obj.foo.y)
		)	
	}


class Example {
	implicit val custom = new CustomJsonFormat()
	//""" {"a": "foobar", "foo": { "x": 5, "y": 100 } } """.parseJson.convertTo[Parent1]
	val x = """ {"a": "foobar", "x": 5, "y": 100 } """.parseJson.convertTo[Parent1]
	println(x)
}
