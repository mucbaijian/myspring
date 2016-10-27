package com.bj.java.lucene;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class FileUtil extends FileUtils {


    private static List<HashMap<String, Object>> fileMaps = new ArrayList<>();

    public static void listFiles2(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        for (File files2 : files) {
            if (files2.isFile()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("path", files2.getAbsolutePath());
                map.put("name", files2.getName());
                map.put("time", files2.lastModified());
                fileMaps.add(map);
            } else {
                listFiles2(files2.getAbsolutePath());
            }
        }
    }


    /**
     * 获取某个目录下面所有的文件信息,包括子孙文件信息
     * 方法名：listFiles
     * 创建人：xuchengfei
     * 时间：2016年6月13日-下午9:41:32
     * 手机:1564545646464
     *
     * @param path
     * @param fileMaps void
     * @throws
     * @since 1.0.0
     */
    public static void listFiles(String path, List<HashMap<String, String>> fileMaps) {
        //查询的目录
        File file = new File(path);
        //获取当前目录下面的文件和目录
        File[] files = file.listFiles();
        for (File files2 : files) {
            //如果是文件
            if (files2.isFile()) {
                if (accept(files2)) {
                    //直接存储文件的，名字，路径，修改时间
                    HashMap<String, String> map = new HashMap<>();
                    map.put("path", files2.getAbsolutePath());
                    map.put("name", files2.getName());
                    map.put("time", String.valueOf(files2.lastModified()));
                    try {
                        if (files2.getName().endsWith("txt")) {
                            String content = FileUtil.readFileToString(files2, "gbk");
                            map.put("content", content);
                        } else {
                            map.put("content", files2.getName());
                        }
                    } catch (Exception e) {
                        map.put("content", "");
                        e.printStackTrace();
                    }
                    //追加到集合中
                    fileMaps.add(map);
                }
            } else {
                //如果是文件夹，继续递归调用
                listFiles(files2.getAbsolutePath(), fileMaps);
            }
        }
    }

    //判断文件后缀
    public static boolean accept(File path) {
        return path.getName().toLowerCase().endsWith(".txt");//只对txt进行索引
    }

    public static void main(String[] args) {
        String path = "E:\\lunceneTest\\test1";
        List<HashMap<String, String>> maps = new ArrayList<>();
        listFiles(path, maps);
        System.out.printf(String.valueOf(maps));
    }


}
