package io.github.amzn.anodizer.codegen.context

/**
 * Helper class for generators.
 *
 * @param A
 * @param R
 */
public interface CtxVisitor<A, R> {

    public fun defaultVisit(ctx: Ctx, args: A): R {
        for (child in ctx.children()) {
            visit(child, args)
        }
        return defaultReturn(ctx, args)
    }

    public fun defaultReturn(ctx: Ctx, args: A): R

    public  fun visit(ctx: Ctx, args: A): R = ctx.accept(this, args)

    public  fun visitModel(ctx: CtxModel, args: A): R = defaultVisit(ctx, args)

    public  fun visitDefinition(ctx: CtxDefinition, args: A): R = when (ctx) {
        is CtxNamespace -> visitNamespace(ctx, args)
        is CtxTypedef -> visitTypedef(ctx, args)
    }

    public  fun visitNamespace(ctx: CtxNamespace, args: A): R = defaultVisit(ctx, args)

    public  fun visitTypedef(ctx: CtxTypedef, args: A): R = when (ctx) {
        is CtxAlias -> visitAlias(ctx, args)
        is CtxEnum -> visitEnum(ctx, args)
        is CtxStruct -> visitStruct(ctx, args)
        is CtxUnion -> visitUnion(ctx, args)
    }

    public  fun visitAlias(ctx: CtxAlias, args: A): R = defaultVisit(ctx, args)

    public  fun visitEnum(ctx: CtxEnum, args: A): R = defaultVisit(ctx, args)

    public  fun visitStruct(ctx: CtxStruct, args: A): R = defaultVisit(ctx, args)

    public  fun visitUnion(ctx: CtxUnion, args: A): R = defaultVisit(ctx, args)
}
