package com.edu.cqupt.shemining.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Log4j2
public class PythonRun {

    @Value("${application.python.environment}")
    private String environment;
    private String root = null;
    private String cache = null;
    private boolean autoRemoveCache = true;
    public String run(String path, List<String> args) throws Exception {
//        path = createNewPy(path);
        List<String> inputArgs = new LinkedList<>(Arrays.asList(environment, path));  //设定命令行
        inputArgs.addAll(args);
        Process proc;
        String line;
        Boolean find = false;
        StringBuilder result = new StringBuilder();
        System.out.println(JSONObject.toJSONString(inputArgs.toArray(new String[0])));
        proc = Runtime.getRuntime().exec(inputArgs.toArray(new String[0]));  //执行py文件
        BufferedReader in = getConsoleReader(proc.getInputStream());
        while ((line = in.readLine()) != null) {

            if (line.equals("digraph g {")){
                find = true;
                continue;
            };
            if (line.equals("}")){
                break;
            };
            if (find){
                String[] split = line.split("  ");
                String replace = split[0].replace("\"", "");
                String[] split1 = replace.split(" -> ");
                for (int i=0; i<split1.length; i++){
                    String resST = split1[i]+",";
                }
                String rep = replace+",";
                result.append(rep);
            };
        }

        int exitValue = proc.waitFor();
        log.info("Python exitValue：" + exitValue);
        if (exitValue == 0) log.info("Python 代码结果：" + result);
        else {
            BufferedReader errorReader = getConsoleReader(proc.getErrorStream());
            while ((line = errorReader.readLine()) != null) {
                log.error(line);
            }
            throw new Exception("脚本运行出错详见日志");
        }
        in.close();
        return result.toString();
    }

    private String createNewPy(String path) throws IOException {
        File file = new File(path);
        if (file.isFile()) {
            String result = loadTxt(file);
            if (root != null) {
                result = "import sys\n" +
                        "sys.path.append(\"" + root + "\")\n" + result;
            }
            String save = cache + file.getName();
            saveTxt(save, result);
            return save;
        }
        return null;
    }

    private static File saveTxt(String filename, String string) {
        File file = new File(filename);
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            out.write(string);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private String loadTxt(File file) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                result.append(str).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public boolean isAutoRemoveCache() {
        return autoRemoveCache;
    }

    public void setAutoRemoveCache(boolean autoRemoveCache) {
        this.autoRemoveCache = autoRemoveCache;
    }

    private BufferedReader getConsoleReader(InputStream inputStream) {
        return new BufferedReader(
                new InputStreamReader(inputStream)
        );
    }
}

