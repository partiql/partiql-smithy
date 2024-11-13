package io.github.amzn.ridl.kitchen

import com.amazon.ion.Decimal
import kotlin.test.Test

/**
 * TODO round-trip testing.
 */
class KitchenTest {

   @Test
   fun primitives() {
      val boxBool = Kitchen.BoxBool(true)
      val boxInt = Kitchen.BoxInt(1)
      val boxFloat = Kitchen.BoxFloat(1.0)
      val boxDecimal = Kitchen.BoxDecimal(Decimal.valueOf(100))
      val boxStr = Kitchen.BoxStr("Hello, world.")
      val boxBlob = Kitchen.BoxBlob("beep boop".toByteArray())
      val boxClob = Kitchen.BoxClob("boop beep".toByteArray())
      println(boxBool.toString())
      println(boxInt.toString())
      println(boxFloat.toString())
      println(boxDecimal.toString())
      println(boxStr.toString())
      println(boxBlob.toString())
      println(boxClob.toString())

      val reader = KitchenReader.text(boxBlob.toString().byteInputStream())
      reader.next()
      val after = reader.readBoxBlob()
      assert(boxBlob.value.contentEquals(after.value))
   }

   @Test
   fun arrays() {
      val arrA = Kitchen.ArrA(listOf(true, false))
      val arrB = Kitchen.ArrB(listOf(true, true, true, true, true, false, false, false, false, false))
      val arrC = Kitchen.ArrC(listOf(Kitchen.BoxBool(true), Kitchen.BoxBool(false)))
      val arrNested = Kitchen.ArrNested(listOf(listOf(true, false), listOf(false, true)))
      println(arrA.toString())
      println(arrB.toString())
      println(arrC.toString())
      println(arrNested.toString())

      val reader = KitchenReader.text(arrA.toString().byteInputStream())
      reader.next()
      val after = reader.readArrA()
      assert(arrA.values == after.values)
   }

   @Test
   fun enums() {
      val enumA = Kitchen.EnumA.GOODNIGHT_MOON
      println(enumA.toString())
   }

   @Test
   fun structs() {
      val structA = Kitchen.StructA(true, 1, "one")
      val structAlias = Kitchen.StructAlias(false, 2, "two")
      val structB = Kitchen.StructB(
         x = listOf(true, false),
         y = listOf(1, 2, 3),
         z = listOf("hello", "goodbye")
      )
      val structC = Kitchen.StructC(
         x = Decimal.valueOf(1),
         y = "beep boop!".toByteArray(),
         z = "boop beep!".toByteArray(),
      )
      println(structA.toString())
      println(structAlias.toString())
      println(structB.toString())
      println(structC.toString())


      val reader = KitchenReader.text(structA.toString().byteInputStream())
      reader.next()
      val after = reader.readStructA()
      assert(structA == after)
   }
}
