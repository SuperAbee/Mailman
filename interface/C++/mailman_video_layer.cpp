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

void decompose(string workplace, string image_prefix, string video_name)
{
    string url;
    url = ("http://localhost:9123/video/decompose?workplace=" + workplace
        + "&imagePrefix=" + image_prefix
        + "&videoName=" + video_name);
    ShellExecute(NULL, L"open", stringToLPCWSTR(url), NULL,NULL, SW_SHOWNORMAL);
}

void compose(string fps, string num_of_pictures, string workplace, string image_prefix, string delete_images)
{
    string url;
    url = ("http://localhost:9123/video/compose?fps= " + fps
        + "&numOfPictures=" + num_of_pictures
        + "&workplace=" + workplace
        + "&imagePrefix=" + image_prefix
        + "&deleteImages=" + delete_images);
    ShellExecute(NULL, L"open", stringToLPCWSTR(url), NULL, NULL, SW_SHOWNORMAL);
}

int main()
{
    decompose("D:/Mailman/Workplace", "decode", "in.mp4");
    compose("5", "19", "D:/Mailman/Workplace", "tmp", "False");
    return 0;
}
