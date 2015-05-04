package org.simple.base.util

import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import org.codehaus.groovy.control.CompilerConfiguration
import org.nutz.log.Log
import org.nutz.log.Logs

/**
 * 脚本解析器
 *
 * @author Jay Wu
 */
class GroovyUtil {

    private transient static Log log = Logs.getLog(GroovyUtil)

    static Map<String, Script> scriptCache = [:]

    static Map<String, Template> templateCache = [:]

    static boolean useIndy() {
        return System.getProperty("java.version").startsWith("1.7")
    }

    private static CompilerConfiguration initCompilerConfiguration() {
        CompilerConfiguration config = new CompilerConfiguration()

        if (useIndy()) {
            //log.debug("Using indy mode")
            //config.getOptimizationOptions().put("indy", true)
            config.getOptimizationOptions().put("int", false)
        }

        return config
    }

    static GroovyScriptEngine createGroovyEngine(ClassLoader classLoader = GroovyUtil.classLoader) {
        GroovyScriptEngine engine = new GroovyScriptEngine(ClassUtil.basePath, classLoader)
        engine.config = initCompilerConfiguration()
        return engine
    }

    static GroovyShell createGroovyShell(Binding binding = new Binding(), ClassLoader classLoader = GroovyUtil.classLoader) {
        CompilerConfiguration config = initCompilerConfiguration()
        GroovyShell shell = new GroovyShell(classLoader, binding, config)
        return shell
    }

    /**
     * 快速执行脚本，不进行缓存
     *
     * @param scriptText
     * @param binding
     * @return
     */
    static def parseScriptText(String scriptText, Binding binding = new Binding()) {
        def shell = new GroovyShell(GroovyUtil.classLoader, binding)
        Script script = shell.parse(scriptText)
        return script.run()
    }

    /**
     * 执行脚本，并对脚本进行缓存
     *
     * @param cacheKey
     * @param scriptText
     * @param binding
     * @return
     */
    static def parseScriptText(String cacheKey, String scriptText, Binding binding = new Binding()) {
        Script script = scriptCache[cacheKey]

        if (!script) {
            script = createGroovyShell().parse(scriptText)
            scriptCache[cacheKey] = script
        }

        script.binding = binding
        def result = script.run()
        script.binding = null
        return result
    }

    /**
     * 执行脚本文件，并对其进行缓存
     *
     * @param path
     * @param binding
     * @return
     */
    static def parseScriptFile(String path, Binding binding = new Binding()) {
        Script script = scriptCache[path]

        if (!script) {
            File file = FilesPlus.findFile(path)

            assert file, "Can't not find any file!"
            assert !file.isDirectory(), "Can not parse directory!"

            def reloadCache = { File f ->
                def s = createGroovyShell().parse(f)
                scriptCache[path] = s
            }

            script = reloadCache(file)

            FilesPlus.addListener(file, new FilesPlus.FileListener() {
                void changed(File target) {
                    reloadCache(target)
                }
            })
        }

        script.binding = binding
        def result = script.run()
        script.binding = null
        return result
    }

    /**
     *  快速执行应用所在运行目录下的脚本
     *
     * @param path 相对路径，相对于应用所在classes目录的上一级
     * @param binding
     * @return
     */
    static def parseBaseScriptFile(String path, Binding binding) {
        return createGroovyEngine().run(path, binding)
    }

    static def evaluate(String scriptText, Binding binding = new Binding(), ClassLoader classLoader = GroovyUtil.classLoader) {
        GroovyShell shell = createGroovyShell(binding, classLoader)
        return shell.evaluate(scriptText)
    }

    /**
     * 根据给定的模板内容创建模板类
     *
     * @param text 模板内容
     * @param needCache 是否需要缓存模板类，默认为true
     * @return 模板类
     */
    static Template createTemplate(String text, boolean needCache = true) {
        Template template = templateCache[text]

        if (!template) {
            def engine = new SimpleTemplateEngine()
            template = engine.createTemplate(text)

            if (needCache) {
                templateCache[text] = template
            }
        }

        return template
    }

    /**
     * 将模板内容解析为最终结果
     *
     * @param text 模板内容
     * @param binding 模板参数
     * @param needCache 是否需要缓存模板类，默认为true
     * @return 最终结果
     */
    static String parseTemplate(String text, Map binding, boolean needCache = true) {
        Template template = createTemplate(text, needCache)
        Writable writable

        if (binding) {
            writable = template.make(binding)
        } else {
            writable = template.make()
        }

        return writable.toString()
    }

    /**
     * 生成简单闭包类实例，并执行
     */
    static <T> T getClosureValue(Class<? extends Closure> clazz, Object owner = null) {
        if (clazz == Closure) {
            return null
        } else {
            Closure c = clazz?.newInstance(this, this)

            if (owner) {
                c.delegate = owner
            }

            return (T) c?.call()
        }
    }
}
