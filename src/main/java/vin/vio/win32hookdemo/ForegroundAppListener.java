package vin.vio.win32hookdemo;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.util.Arrays;
import java.util.List;

import static com.sun.jna.platform.win32.WinRas.MAX_PATH;

public class ForegroundAppListener {

    public interface User32 extends Library {
        User32 INSTANCE = Native.load("user32", User32.class);

        WinDef.HWND GetForegroundWindow();
        int GetWindowTextA(WinDef.HWND hWnd, byte[] lpString, int nMaxCount);
        int GetWindowTextLengthA(WinDef.HWND hWnd);
        int GetClassNameA(WinDef.HWND hWnd, byte[] lpClassName, int nMaxCount);
        boolean GetWindowThreadProcessId(WinDef.HWND hWnd, IntByReference lpdwProcessId);
        int MessageBoxA(WinDef.HWND hWnd, String lpText, String lpCaption, int uType);
    }

    public interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        WinNT.HANDLE OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);
        boolean CloseHandle(WinNT.HANDLE hObject);
    }
}
