package com.example.codegenerator

import com.squareup.kotlinpoet.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.*
import javax.lang.model.util.SimpleElementVisitor8
import javax.tools.Diagnostic

class MethodVisitor(
    private val processingEnvironment: ProcessingEnvironment
) : SimpleElementVisitor8<Unit, TypeSpec.Builder>() {

    override fun visitExecutable(p0: ExecutableElement, p1: TypeSpec.Builder) {
        processingEnvironment.messager.printMessage(Diagnostic.Kind.WARNING, "${p0.annotationMirrors[1].elementValues}\r\n")
        p1.addFunction(
            FunSpec.builder(p0.simpleName.toString())
                .addModifiers(KModifier.OVERRIDE)
                .returns(
                    // Call
                    ClassName.bestGuess(processingEnvironment.typeUtils.asElement(p0.returnType).simpleName.toString())
                )
                .addCode(
                    CodeBlock.of(
                        """
                      return OkHttpClient().newCall(Request.Builder().url(%S).build())
                    """.trimIndent(),
                        //     @GET("https://api.opendota.com/api/heroes")
                        p0.annotationMirrors[1].elementValues.values.first().value
                    )
                )
                .build()
        )
    }
}