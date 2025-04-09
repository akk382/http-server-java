```
Response body

A response body is used to return content to the client. This content may be an entire web page, a file, a string, or anything else that can be represented with bytes.

Your /echo/{str} endpoint must return a 200 response, with the response body set to given string, and with a Content-Type and Content-Length header.

Here's an example of an /echo/{str} request:

GET /echo/abc HTTP/1.1\r\nHost: localhost:4221\r\nUser-Agent: curl/7.64.1\r\nAccept: */*\r\n\r\n

And here's the expected response:

HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: 3\r\n\r\nabc

Here's a breakdown of the response:

// Status line
HTTP/1.1 200 OK
\r\n                          // CRLF that marks the end of the status line

// Headers
Content-Type: text/plain\r\n  // Header that specifies the format of the response body
Content-Length: 3\r\n         // Header that specifies the size of the response body, in bytes
\r\n                          // CRLF that marks the end of the headers

// Response body
abc                           // The string from the request

The two headers are required for the client to be able to parse the response body. Note that each header ends in a CRLF, and the entire header section also ends in a CRLF.
```