package example

import spray.json._

/* 
https://groups.google.com/forum/#!topic/spray-user/h1Fbnby3xAM
I have a case class that is serialized/deserialized through spray-json. 
Certain property needs to be set to some default value if not found in 
the request, but I couldn't find a way to do so, except write my own 
serializer extending  RootJsonFormat[MyClass]. Which is a bit cumbersome.
*/


case class Foo(baz: Int, bar: String)

case class OptionalFoo(baz: Option[Int], bar: Option[String])

object Foo extends DefaultJsonProtocol {
 implicit val format: RootJsonFormat[Foo] = jsonFormat2(Foo.apply)
}

object OptionalFoo extends DefaultJsonProtocol {
	implicit val format: RootJsonFormat[OptionalFoo] = jsonFormat2(OptionalFoo.apply)
}

object FooConversions {
	implicit class OptConversionKlazz(opt: OptionalFoo) {
		def toFoo : Foo = {
			Foo(opt.baz.getOrElse(0), opt.bar.getOrElse("DefaultString"))
		}
	}
	implicit class FooConversionKlazz(foo: Foo) {
		def toOptionalFoo : OptionalFoo = {
			OptionalFoo(Some(foo.baz), Some(foo.bar))
		}
	}
}

/* 
scala> import example._
import example._

scala> import FooConversions._
import FooConversions._

scala> val f = Foo(1,"hello")
f: example.Foo = Foo(1,hello)

scala> import spray.json._
import spray.json._

scala> f.toJson
res0: spray.json.JsValue = {"baz":1,"bar":"hello"}

scala> f.toJson.convertTo[OptionalFoo]
res1: example.OptionalFoo = OptionalFoo(Some(1),Some(hello))

scala> def x(foo: Foo) { println(foo) }
x: (foo: example.Foo)Unit

scala> x(res1.toFoo)
Foo(1,hello)
*/

object ImplicitFunctionFun {
	import scala.language.implicitConversions
	implicit def implicitConversionOfOptToFoo(opt: OptionalFoo) : Foo = {
		Foo(opt.baz.getOrElse(0), opt.bar.getOrElse("DefaultString"))
	}
}

/*
scala> import example._
import example._

scala> import spray.json._
import spray.json._

scala> val baz = """{"baz" : 2 }""".parseJson.convertTo[OptionalFoo]
baz: example.OptionalFoo = OptionalFoo(Some(2),None)

scala> def x(foo: Foo) { println(foo) }
x: (foo: example.Foo)Unit

//Expected error
scala> x(baz)
<console>:19: error: type mismatch;
 found   : example.OptionalFoo
 required: example.Foo
       x(baz)
         ^

//But wait! Implicits!
scala> import ImplicitFunctionFun._
import ImplicitFunctionFun._

scala> x(baz)
Foo(2,DefaultString)

*/