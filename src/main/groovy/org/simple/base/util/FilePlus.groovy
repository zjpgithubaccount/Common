package org.simple.base.util

import org.nutz.lang.Files
import org.nutz.lang.Lang
import org.nutz.log.Log
import org.nutz.log.Logs
import org.simple.base.util.ListUtil
import org.simple.base.util.StreamsPlus
import org.simple.base.util.StringUtil

public class FilesPlus extends Files {

    private static final Log log = Logs.get()

    /**
     * 读取 enc编码 文件全部内容
     *
     * @param f   文件
     * @param enc 编码
     * @return 文件内容
     */
    public static String read(File f, String enc) {
        return Lang.readAll(StreamsPlus.fileInr(f, enc))
    }

    /**
     * 读取 enc编码 文件全部内容
     *
     * @param path 文件路径
     * @param enc  编码
     * @return 文件内容
     */
    public static String read(String path, String enc) {
        File f = findFile(path)
        if (null == f)
            throw Lang.makeThrow("Can not find file '%s'", path)
        return read(f, enc)
    }

    public static boolean isOldFile(String filePath, int maxAge) {
        File f = new File(filePath)
        boolean old = true

        if (f.exists()) {
            Date now = new Date()
            if ((now.getTime() - f.lastModified()) / 1000 < maxAge) {
                old = false
            }
        }

        return old
    }

    /**
     * 递归获取一个目录下所有的文件。隐藏文件会被忽略。
     *
     * @param dir    目录
     * @param suffix 文件后缀名。如果为 null，则获取全部文件
     * @return 文件数组
     */
    public static List<File> scanFiles(String dir, final String suffix) {
        List<File> list = new ArrayList<File>()

        File dirFile = findFile(dir)
        for (File file : scanDirs(dirFile)) {
            list.addAll(ListUtil.array2List(files(file, suffix)))
        }

        return list
    }

    static class FileMonitor {

        private Timer timer

        private Map<File, Long> files

        private List<FileListener> listeners

        private long pollingInterval = 2000l

        private static FileMonitor instance = new FileMonitor()

        private FileMonitor() {
            this(null)
        }

        public static FileMonitor getInstance() {
            return instance
        }

        private FileMonitor(Long pollingInterval) {
            files = new HashMap<File, Long>()
            listeners = new ArrayList<FileListener>()
            timer = new Timer(true)

            if (pollingInterval == null) {
                pollingInterval = this.pollingInterval
            }

            start()
        }

        public void start() {
            timer.scheduleAtFixedRate(new FileMonitorNotifier(), pollingInterval, pollingInterval)
        }

        public void stop() {
            timer.cancel()
        }

        public void add(String filePath, FileListener listener) {
            add(findFile(filePath), listener)
        }

        public void add(File f, FileListener listener) {
            if (f.isDirectory()) {
                listener.dir = f
            } else {
                listener.file = f
            }

            listener.key = f.getAbsolutePath()

            addFile(f)
            addListener(listener)
        }

        private FileMonitor addFile(File file) {
            if (file == null || !file.exists()) {
                return this
            }

            if (file.isDirectory()) {
                File[] files = file.listFiles()
                if (files != null) {
                    for (File f : files) {
                        addFile(f)
                    }
                }
            } else {
                if (!files.containsKey(file)) {
                    log.info("开始监控文件：" + file.getName())
                    files.put(file, file.lastModified())
                }
            }

            return this
        }

        private FileMonitor addListener(FileListener s) {
            removeListener(s.key)
            listeners.add(s)
            return this
        }

        public FileMonitor removeListener(String key) {
            FileListener c = null

            for (FileListener listener : listeners) {

                if (StringUtil.equalsIgnoreCase(listener.key, key)) {
                    c = listener
                    break
                }
            }

            if (c != null) {
                listeners.remove(c)
            }

            return this
        }

        private class FileMonitorNotifier extends TimerTask {

            public void run() {
                for (File file : files.keySet()) {
                    long lastModifiedTime = files.get(file)
                    long newModifiedTime = file.exists() ? file.lastModified() : -1

                    if (newModifiedTime != lastModifiedTime) {
                        log.info(file.getName() + " changed!")

                        files.put(file, newModifiedTime)

                        for (FileListener listener : listeners) {
                            String target = listener.dir == null ? file.getAbsolutePath() : file.getParent()

                            if (listener.key.equalsIgnoreCase(target)) {
                                listener.changed(file)
                            }
                        }
                    }
                }
            }
        }
    }

    public abstract static class FileListener {

        public String key

        public File dir

        public File file

        public abstract void changed(File target)
    }

    public static void addListener(String filePath, FileListener listener) {
        addListener(findFile(filePath), listener)
    }

    public static void addListener(File file, FileListener listener) {
        FileMonitor.getInstance().add(file, listener)
    }
}
