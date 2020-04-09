#include<windows.h>                      
#include<shellapi.h> 
#include<iostream>
#include<string>
using namespace std;

//类型转换 将xsd:string 转为 LPCSWTR
LPCWSTR stringToLPCWSTR(std::string orig)
{
    size_t origsize = orig.length() + 1;
    const size_t newsize = 100;
    size_t convertedChars = 0;
    wchar_t* wcstring = (wchar_t*)malloc(sizeof(wchar_t) * (orig.length() - 1));
    mbstowcs_s(&convertedChars, wcstring, origsize, orig.c_str(), _TRUNCATE);

    return wcstring;
}

void encode(string srcFilePath, string encodedFilePath, string time)
{
    string url;
    url = ("http://localhost:9123/test/encode?srcFilePath=" + srcFilePath
        + "&encodedFilePath=" + encodedFilePath
        + "&time=" + time);
    ShellExecute(NULL, L"open", stringToLPCWSTR(url), NULL, NULL, SW_SHOWNORMAL);
}

string change(string a)
{
    for (int i = 0; i < a.length(); i++)
    {
        if (a[i] == '\\')
        {
            a[i] = '/';
        }
    }
    return a;
}

int main(int argc, char *argv[])
{
    string srcFilePath=argv[1];
    srcFilePath = change(srcFilePath);
    string encodedFilePath=argv[2];
    encodedFilePath = change(encodedFilePath);
    string time=argv[3];
    time = change(time);
    cout << srcFilePath << encodedFilePath << time;
    encode(srcFilePath, encodedFilePath, time);

    return 0;
}
