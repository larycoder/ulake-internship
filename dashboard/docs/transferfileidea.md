# File upload/download from dashboard

There are 2 actions: upload file to storage, download file from storage. For
query only, download is more important than upload. The basic method to download
file from html is applying anchor with download attribute. However, the anchor
tag does not support header modification, hence the traditional anchor hyperlink
does not support authorization token in header. Then it is required to find
another mechanism to pass token to server.

## Solution (2020-04-14)

There download resource in endpoint "/api/object/{cid}/data" retrieve token from
session cookie of site following format:

```
Authorization: {token without "Bearer"}
```

That is responsibility of front-end to setup and pass this cookie to server
endpoint. The server itself is stateless and will not save any token by itself.
The simple idea is that pass token from local storage and generate session
cookie for whole site as below:

```
document.cookie = "Authorization={token}; path=/;"
```

**NOTE** this is only example of the way front-end could do, more effort is
required.
