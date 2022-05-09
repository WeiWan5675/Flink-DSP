package com.weiwan.dsp.console;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.console.model.entity.Flow;
import org.junit.Test;

import javax.script.*;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/4/8 18:17
 * @description
 */
public class NashornTest {

    @Test
    public void testInvocable() throws ScriptException, FileNotFoundException, NoSuchMethodException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        FileReader fileReader = new FileReader("F:\\project\\Flink-DSP\\dsp-console\\src\\test\\java\\com\\weiwan\\dsp\\console\\test.js");
        engine.eval(fileReader);

        Bindings bindings = engine.createBindings();
        Flow flow = new Flow();
        flow.setFlowName("abc");
        bindings.put("record", flow);

        Invocable invocable = (Invocable) engine;
        Object result = invocable.invokeFunction("a", flow);
        Boolean b = (Boolean) result;
        System.out.println(b);
    }

    @Test
    public void testCompilable() throws ScriptException, FileNotFoundException, NoSuchMethodException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        FileReader fileReader = new FileReader("F:\\project\\Flink-DSP\\dsp-console\\src\\test\\java\\com\\weiwan\\dsp\\console\\test.js");
        Compilable compilable = (Compilable) engine;
        CompiledScript compiledScript = compilable.compile(fileReader);

        Bindings bindings = engine.createBindings();
        Flow flow = new Flow();
        flow.setFlowName("123");
        bindings.put("record", flow);

        Object eval = compiledScript.eval(bindings);
    }

    @Test
    public void testSplitPlugin() {
        Flow flow = new Flow();
        flow.setFlowName("abc");
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(flow);
        System.out.println(jsonObject);
    }

    @Test
    public void test() {
        Object b = true;
        if (b instanceof Boolean) {
            System.out.println((boolean) b);
        }
    }
}
