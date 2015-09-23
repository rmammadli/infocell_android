using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace InfoCell.Classes
{
    public static class HttpHelper
    {


        static HttpHelper()
        {
        }


        public static Task<string> DownloadString(string url, string body, CancellationTokenSource _cancelProcess)
        {
            TaskCompletionSource<string> _compl = new TaskCompletionSource<string>();


            if (_cancelProcess == null)
            {
                _cancelProcess = new CancellationTokenSource();
            }

            Task.Factory.StartNew(async () =>
            {
                string result = null;
                try
                {
                    HttpWebRequest req = WebRequest.CreateHttp(url);
                    req.Method="POST";
                    var tt=await Task<Stream>.Factory.FromAsync(req.BeginGetRequestStream,
                                    req.EndGetRequestStream, req);

                    using (StreamWriter stream = new StreamWriter(tt))
                    {
                        stream.Write(body);
                    }

                    var tsk = Task<WebResponse>.Factory.FromAsync(req.BeginGetResponse,
                                    req.EndGetResponse, req);

                    if (!tsk.Wait(15000, _cancelProcess.Token))
                    {
                        throw new Exception("Timeout");
                    }

                    HttpWebResponse resp = tsk.Result as HttpWebResponse;

                    if (resp != null)
                    {

                        MemoryStream ms = new MemoryStream();
                        using (var file = resp.GetResponseStream())
                        {
                            byte[] buf = new byte[2048];
                            int readed = 0;

                            while (true)
                            {
                                readed = file.Read(buf, 0, buf.Length);

                                if (readed == 0)
                                {
                                    break;
                                }

                                ms.Write(buf, 0, readed);
                            }
                        }

                        result = UnicodeEncoding.UTF8.GetString(ms.GetBuffer(), 0, (int)ms.Length);




                    }
                }
                catch
                {

                }
                _compl.SetResult(result);
            });

            return _compl.Task;
        }
    }
}
