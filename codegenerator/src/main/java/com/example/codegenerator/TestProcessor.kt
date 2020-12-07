package com.example.codegenerator

import com.example.codegenerator.TestProcessor.Companion.KAPT_KOTLIN_GENERATED_OPTION_NAME
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.lang.Exception
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic
import javax.tools.JavaFileManager
import javax.tools.StandardLocation

@SupportedAnnotationTypes("com.example.lib.RetrofitService")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class TestProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    private val initClass = FileSpec.builder("", "Init")
        .addImport("com.example.lib", "ServiceHolder")

    override fun process(
        elements: MutableSet<out TypeElement>,
        roundEnvironment: RoundEnvironment
    ): Boolean {
        val initClass = TypeSpec.classBuilder("Init")
        if (roundEnvironment.processingOver()) return true

        roundEnvironment
            .getElementsAnnotatedWith(elements.first())
            .forEach {
                try {
                    val type = processingEnv.getTypeUtils().getWildcardType(
                        processingEnv.getElementUtils().getTypeElement(
                            ArrayList::class.java.getName()
                        ).asType(), null
                    )
                    val file = FileSpec.builder("", "${it.simpleName}Impl")
                        .addImport("okhttp3", listOf("Call", "Request", "OkHttpClient"))

                    val service = TypeSpec.classBuilder("${it.simpleName}Impl")
                        .addSuperinterface(ClassName.bestGuess(it.simpleName.toString()))

                    this.initClass.addImport(
                        processingEnv.elementUtils.getPackageOf(it).toString(),
                        it.simpleName.toString()
                    )

                    it.enclosedElements.forEach {
                        if (it.kind == ElementKind.METHOD) {
                            it.accept(MethodVisitor(processingEnv), service)
                        }
                    }

                    val serviceClass = service.build()
                    initClass.addInitializerBlock(
                        createInitBlock(
                            it.simpleName,
                            serviceClass.name!!
                        )
                    )
                    processingEnv.elementUtils.getTypeElement(String::class.java.canonicalName)
                    file.addImport(
                        processingEnv.elementUtils.getPackageOf(it).toString(),
                        it.simpleName.toString()
                    )
                        .addType(serviceClass).build()
                        .writeTo(processingEnv.filer)
                } catch (ex: Exception) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.WARNING,
                        "${ex}\r\n"
                    )
                }
            }
        this.initClass.addType(initClass.build()).build().writeTo(processingEnv.filer)
        return true
    }


    private fun createInitBlock(service: Name, serviceImpl: String): CodeBlock {
        return CodeBlock.of(
            """
                        ServiceHolder.init(%N::class to %N())
                    """.trimIndent(),
            service, serviceImpl
        )
    }
}