package vin.vio.win32hookdemo;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import static com.sun.jna.platform.win32.WinRas.MAX_PATH;

@SpringBootApplication
@Slf4j
public class Win32HookDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(Win32HookDemoApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void afterStartup() {
        while (true) {
            // 获取当前前台窗口句柄
            WinDef.HWND hwnd = ForegroundAppListener.User32.INSTANCE.GetForegroundWindow();

            // 获取窗口标题长度
            int length = ForegroundAppListener.User32.INSTANCE.GetWindowTextLengthA(hwnd);
            byte[] windowText = new byte[length + 1];
            ForegroundAppListener.User32.INSTANCE.GetWindowTextA(hwnd, windowText, windowText.length);
            String appName = Native.toString(windowText);
            log.info("窗口标题: {}", appName);

            // 获取进程ID
            IntByReference pid = new IntByReference();
            ForegroundAppListener.User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);
            int processId = pid.getValue();
            log.info("PID：{}", processId);
            Kernel32 kernel32 = Kernel32.INSTANCE;
            WinNT.HANDLE hProcess = kernel32.OpenProcess(WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ, false, processId);
            Psapi psapi = Psapi.INSTANCE;
            if (hProcess != null) {
                byte[] pathBuffer = new byte[MAX_PATH];

                if (psapi.GetModuleFileNameExA(hProcess, null, pathBuffer, pathBuffer.length - 1) > 0) {
                    String processPath = Native.toString(pathBuffer);
                    log.info("执行文件地址: {}", processPath);
                    kernel32.CloseHandle(hProcess);
                }

                kernel32.CloseHandle(hProcess);
            }

            try {
                // 暂停一段时间以减少 CPU 使用率
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
