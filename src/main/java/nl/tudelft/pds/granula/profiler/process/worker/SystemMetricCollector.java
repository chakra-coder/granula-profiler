package nl.tudelft.pds.granula.profiler.process.worker;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;

public abstract class SystemMetricCollector {

    String path;
    int frequency;
    long stopTime;
    RandomAccessFile reader;

    public SystemMetricCollector(String path, int frequency, long stopTime) {
        this.path = path;
        this.frequency = frequency;
        this.stopTime = stopTime;
    }

    public void open() {
        try {
            reader = new RandomAccessFile(path, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        IOUtils.closeQuietly(reader);
    }

    public abstract void collectOnce() throws IOException;

    public void collect() {
        try {
            while(!(System.currentTimeMillis() > stopTime)) {
                reader.seek(0);
                collectOnce();
                Thread.sleep(frequency);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String collectFromExecution(String program, String parameter) {

        String output = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(new PumpStreamHandler(baos));
            executor.setExitValues(null);

            CommandLine commandLine = new CommandLine(program);
            commandLine.addArgument(parameter, false);
            executor.execute(commandLine);
            output = baos.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }
}

