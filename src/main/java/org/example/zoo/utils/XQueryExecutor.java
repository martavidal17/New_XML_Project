package org.example.zoo.utils;

import net.sf.saxon.s9api.*;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class XQueryExecutor {
    private static final String XML_PATH = "src/main/resources/zoo.xml";
    private static final String XQUERY_PATH = "src/main/resources/xqueries/";

    private Processor processor;
    private XQueryCompiler compiler;

    public XQueryExecutor() {
        processor = new Processor(false);
        compiler = processor.newXQueryCompiler();
    }

    public String executeQuery(String queryFile, Map<String, String> params) {
        try {
            // Cargar la consulta XQuery
            XQueryExecutable query = compiler.compile(new File(XQUERY_PATH + queryFile));

            // Preparar el ejecutor de la consulta
            XQueryEvaluator evaluator = query.load();
            evaluator.setSource(new StreamSource(new File(XML_PATH)));

            // Agregar parámetros dinámicos a la consulta
            for (Map.Entry<String, String> entry : params.entrySet()) {
                evaluator.setExternalVariable(new QName(entry.getKey()), new XdmAtomicValue(entry.getValue()));
            }

            // Ejecutar la consulta y obtener el resultado
            Serializer out = processor.newSerializer();
            out.setOutputProperty(Serializer.Property.METHOD, "xml");
            out.setOutputProperty(Serializer.Property.INDENT, "yes");
            evaluator.run(out);

            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "<error>Error ejecutando XQuery</error>";
        }
    }
}
