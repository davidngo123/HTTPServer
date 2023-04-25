

## Stories/rubric
This assignment is worth 10 points, total. Your server must:

* **Listen on port 8080.** *(If you use a different port, notify the TA before grading.)*
* **Respond to the GET request. (2 pts)** Have your server respond as any HTTP server would: find the relative file given in the URL portion of the HTTP RequestLine and send the contents back as the body of an HTTP response. Make sure you have `Content-Length` and `Content-Type` set correctly!
* **Respond to the PUT request. (2 pts)** For this, have your server take the body sent along with the PUT request and write it to a new file given by the URL portion of the HTTP RequestLine. (In other words, `PUT /test.txt HTTP/1.0` should write the body to a new file called "test.txt" in the same directory as this code.) Make sure you honor `Content-Length` crrectly in the incoming request.
* **Respond to the POST request. (2 pts)** This is similar to the PUT request, except that the body should be *appended* to the existing file (if any).
* **Respond to the DELETE request. (2 pts)** Have your server delete the file given in the URL portion of the HTTP RequestLine.
* **Respond with HTTP error codes as appropriate. (2 pts)** For example, for a GET or a DELETE request, if no such file is present, return a 404. Implement any other error codes that seem relevant, using your best judgement.

## Extra credit

You can earn a few extra credit points on this assignment:

* **Implement OPTIONS. (1 pt)** For a given URL, determine what HTTP verbs would be supported here: if the file exists, then we can GET it, POST it, and DELETE it. If the file doesn't exist, we can PUT it or POST it.
* **Implement HEAD. (1 pt)** This is like a GET, except it will return only the ResponseLine and any headers in the response.
* **Implement HttpCats. (1 pt)** When sending back any error response (4xx or 5xx), send back HTML content as part of the body, using the HTTPCats image in the HTML body.

        <html><body><h1>501 - Not Implemented</h1><img src="https://http.cat/501"</img></body></html>

## Testing
The `curl` utility is a popular command-line testing tool, and you can use it to drive your HTTP server like so:

* GET a particular resource: `curl http://localhost:8080/test.txt`
* PUT a particular resource: `curl -X PUT --data-ascii "This is a test" http://localhost:8080/test.txt`
* POST a particular resource: `curl --data-ascii "This is a test" http://localhost:8080/text.txt`
* DELETE a particular resource: `curl -X DELETE http://localhost:8080/test.txt`

