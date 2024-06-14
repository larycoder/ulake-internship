import http from 'k6/http';
import { sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Login test
export const options = {
  stages: [
    { duration: '10s', target: 100 }, // Start with 100 VU for 10 seconds
    { duration: '20s', target: 200 }, // Ramp up to 200 VU for 20 seconds
    { duration: '10s', target: 100 }, // Ramp down to 100 VU for 10 seconds
  ],
};

const payload = JSON.stringify({
  userName: 'admin',
  password: 'admin',
});

export default function() {
  const response = http.post(
    'http://dashboard.ulake.usth.edu.vn/api/user/login',
    payload,
    {
      headers: {
        'accept': '*/*',
        'Content-Type': 'application/json',
      },
      tags: { name: 'rest_login' }, // Add a tag for Grafana visualization
    }
  );
}