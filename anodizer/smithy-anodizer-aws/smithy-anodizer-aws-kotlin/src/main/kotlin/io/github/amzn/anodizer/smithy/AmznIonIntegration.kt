package io.github.amzn.anodizer.smithy

import io.github.amzn.anodizer.smithy.protocols.AmznIon1_0
import software.amazon.smithy.kotlin.codegen.integration.KotlinIntegration
import software.amazon.smithy.kotlin.codegen.rendering.protocol.ProtocolGenerator

/**
 * TODO
 */
public class AmznIonIntegration : KotlinIntegration {

    override val order: Byte = 0

    override val protocolGenerators: List<ProtocolGenerator> = listOf(AmznIon1_0())
}
