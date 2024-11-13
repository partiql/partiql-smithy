package io.github.amzn.anodizer.smithy.protocols

import io.github.amzn.anodizer.smithy.traits.AmznIon1_0Trait
import software.amazon.smithy.kotlin.codegen.aws.protocols.core.AwsHttpBindingProtocolGenerator
import software.amazon.smithy.kotlin.codegen.aws.protocols.json.AwsJsonHttpBindingResolver
import software.amazon.smithy.kotlin.codegen.core.KotlinWriter
import software.amazon.smithy.kotlin.codegen.core.RuntimeTypes
import software.amazon.smithy.kotlin.codegen.rendering.protocol.HttpBindingResolver
import software.amazon.smithy.kotlin.codegen.rendering.protocol.ProtocolGenerator
import software.amazon.smithy.kotlin.codegen.rendering.serde.JsonParserGenerator
import software.amazon.smithy.kotlin.codegen.rendering.serde.JsonSerializerGenerator
import software.amazon.smithy.kotlin.codegen.rendering.serde.StructuredDataParserGenerator
import software.amazon.smithy.kotlin.codegen.rendering.serde.StructuredDataSerializerGenerator
import software.amazon.smithy.model.Model
import software.amazon.smithy.model.shapes.OperationShape
import software.amazon.smithy.model.shapes.ServiceShape
import software.amazon.smithy.model.shapes.ShapeId
import software.amazon.smithy.model.traits.TimestampFormatTrait

/**
 * The anodized protocol.
 */
@Suppress("ClassName")
public class AmznIon1_0 : AwsHttpBindingProtocolGenerator() {

    override val protocol: ShapeId = AmznIon1_0Trait.ID

    override val defaultTimestampFormat: TimestampFormatTrait.Format = TimestampFormatTrait.Format.EPOCH_SECONDS

    override fun renderDeserializeErrorDetails(
        ctx: ProtocolGenerator.GenerationContext,
        op: OperationShape,
        writer: KotlinWriter,
    ) {
        writer.write("#T.deserialize(call.response.headers, payload)", RuntimeTypes.AwsJsonProtocols.RestJsonErrorDeserializer)
    }

    override fun getProtocolHttpBindingResolver(model: Model, serviceShape: ServiceShape): HttpBindingResolver {
        return AwsJsonHttpBindingResolver(model, serviceShape, "application/x-amzn-ion-1.0")
    }

    override fun structuredDataParser(ctx: ProtocolGenerator.GenerationContext): StructuredDataParserGenerator {
        // TODO REPLACE ME
        return JsonParserGenerator(this)
    }

    override fun structuredDataSerializer(ctx: ProtocolGenerator.GenerationContext): StructuredDataSerializerGenerator {
        // TODO REPLACE ME
        return JsonSerializerGenerator(this)
    }
}
