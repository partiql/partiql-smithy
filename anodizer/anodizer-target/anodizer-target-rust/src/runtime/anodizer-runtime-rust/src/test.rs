#[cfg(test)]
mod tests {
    use std::io::stdout;
    use crate::example::{BoxBool, KitchenFactory, KitchenWriter, KitchenWriterBuilder, StructA};

    #[test]
    fn sanity() {

        let sink = stdout();
        let mut writer = KitchenWriterBuilder::text(sink);

        let v: BoxBool = true;
        let _ = writer.write_box_bool(&v);

        let v = StructA {
            x: true,
            y: 7,
            z: "Hello!".to_string(),
         };
        let _ = writer.write_struct_a(&v);

        let kitchen = KitchenFactory::new();
        let v = kitchen.struct_a(true, 7, "Hello".to_string());
        let _ = writer.write_struct_a(v);

        // let result = 2 + 2;
        // assert_eq!(result, 4);
    }
}
