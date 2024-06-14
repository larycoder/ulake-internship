from locust import HttpUser, TaskSet, task, between, constant
import json
import random
import string

# TaskSet containing our load test tasks
class UserBehavior(TaskSet):
    @task
    def post_file(self):
        boundary = ''.join(random.choices(string.ascii_letters + string.digits, k=16))
        file_info = { "mime": "text/plain", "name": "test_file", "ownerId": 2004, "size": 813050 }
        file_path = '/home/novete36/Downloads/test_file.txt'

        multipart_data = (
            f'--{boundary}\r\n'
            'Content-Disposition: form-data; name="fileInfo"\r\n'
            'Content-Type: application/json\r\n\r\n'
            f'{json.dumps(file_info)}\r\n'
            f'--{boundary}\r\n'
            'Content-Disposition: form-data; name="file"; filename="test_file.txt"\r\n'
            'Content-Type: application/octet-stream\r\n\r\n'
        ).encode('utf-8')
        
        with open(file_path, 'rb') as f:
            multipart_data += f.read()
        
        multipart_data += f'\r\n--{boundary}--\r\n'.encode('utf-8')

        headers = {
            'Authorization': f'Bearer {self.user.token}',
            'Content-Type': f'multipart/form-data; boundary={boundary}'
        }
        
        self.client.post("/api/file", data=multipart_data, headers=headers, name="post_file")

class WebsiteUser(HttpUser):
    tasks = [UserBehavior]
    wait_time = constant(0)  # No waiting time
    token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3NvbnRnLm5ldC9pc3N1ZXIiLCJ1cG4iOiJ0dW5nbmd2aWV0MzZAZ21haWwuY29tIiwiZ3JvdXBzIjpbIlVzZXIiLCJBZG1pbiJdLCJhdXRoX3RpbWUiOjE3MTYzOTAwMjQ0NTEsInN1YiI6IjEiLCJpYXQiOjE3MTYzOTAwMjQsImV4cCI6MTcxNjQ3NjQyNCwianRpIjoiNWVmYWRlYWYtMmJlOC00NDJiLTgzYTMtOTE5NTMxYWRkMTI0In0.2yyZ0YPr9ImKOp3cSxnuuV1i8oZPyXzGMJGPEhaPxl8ot0WbceDjRDvZOT-Udk6pWR5jkvJHINFHbYmxvXDcs4K7czgQes_yLVMk33pzKtVfY0hd_Y-bHfcPPAUtaeSayO_waK4FEj1IfNDrjNZGmbDO3_-nCUCKReqN44l1FsfUs-K_NVe3s335-cAEI3q2NqjgdB3ft65Dne_ako-cG9w2310WMgIuJHhzYJshzWzMYYeT061OHK7fvETTX36-WT8qm9Q9rdXUixq8SNxyngmjcWpGUVrptd6T7DvsMYYgtJfj9qjs7PZDHsD5uyhbAT9JIE7QZae28XwLQ9WP4w"

    def on_start(self):
        self.client.headers.update({'Authorization': f'Bearer {self.token}'})
