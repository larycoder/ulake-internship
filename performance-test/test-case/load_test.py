from locust import HttpUser, TaskSet, task, between, events
import json
import random
import string
import httpx
from statistics import mean

# Custom listener to collect metrics
all_response_times = []
all_request_counts = []

@events.request.add_listener
def request_listener(request_type, name, response_time, response_length, response, context, exception, start_time, url, **kwargs):
    if exception is None:
        all_response_times.append(response_time)
        all_request_counts.append(1)

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
            'Content-Type': f'multipart/form-data; boundary=--------------------------{boundary}'
        }

        # Use HTTPX for HTTP/2
        with httpx.Client(http2=True) as client:
            response = client.post(
                "http://dashboard.ulake.usth.edu.vn/api/file",
                headers=headers,
                content=multipart_data
            )

        print("Version ", response.http_version)

class WebsiteUser(HttpUser):
    tasks = [UserBehavior]
    wait_time = between(0, 1)  # Optional, no wait time or a fixed wait time between requests
    token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3NvbnRnLm5ldC9pc3N1ZXIiLCJ1cG4iOiJ0dW5nbmd2aWV0MzZAZ21haWwuY29tIiwiZ3JvdXBzIjpbIlVzZXIiLCJBZG1pbiJdLCJhdXRoX3RpbWUiOjE3MTYzOTAwMjQ0NTEsInN1YiI6IjEiLCJpYXQiOjE3MTYzOTAwMjQsImV4cCI6MTcxNjQ3NjQyNCwianRpIjoiNWVmYWRlYWYtMmJlOC00NDJiLTgzYTMtOTE5NTMxYWRkMTI0In0.2yyZ0YPr9ImKOp3cSxnuuV1i8oZPyXzGMJGPEhaPxl8ot0WbceDjRDvZOT-Udk6pWR5jkvJHINFHbYmxvXDcs4K7czgQes_yLVMk33pzKtVfY0hd_Y-bHfcPPAUtaeSayO_waK4FEj1IfNDrjNZGmbDO3_-nCUCKReqN44l1FsfUs-K_NVe3s335-cAEI3q2NqjgdB3ft65Dne_ako-cG9w2310WMgIuJHhzYJshzWzMYYeT061OHK7fvETTX36-WT8qm9Q9rdXUixq8SNxyngmjcWpGUVrptd6T7DvsMYYgtJfj9qjs7PZDHsD5uyhbAT9JIE7QZae28XwLQ9WP4w"

    def on_start(self):
        self.client.headers.update({'Authorization': f'Bearer {self.token}'})

    def on_stop(self):
        global all_response_times, all_request_counts
        # Output metrics when the test stops
        if all_response_times:
            print("Average Response Time:", mean(all_response_times))
        if all_request_counts:
            print("Total Requests:", sum(all_request_counts))
            print("Throughput (requests per second):", sum(all_request_counts) / 30)  # Assuming 30 seconds test duration

if __name__ == "__main__":
    import os
    os.system("locust -f load_test.py --headless -u 100 -r 30 --run-time 30s -H http://dashboard.ulake.usth.edu.vn")
