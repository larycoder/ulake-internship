import http from 'k6/http';
import { sleep } from 'k6';
import { Rate } from 'k6/metrics';

// export const options = {
//   // A number specifying the number of VUs to run concurrently.
//   vus: 1000,
//   // A string specifying the total duration of the test run.
//   duration: '30s',

//   // The following section contains configuration options for execution of this
//   // test script in Grafana Cloud.
//   //
//   // See https://grafana.com/docs/grafana-cloud/k6/get-started/run-cloud-tests-from-the-cli/
//   // to learn about authoring and running k6 test scripts in Grafana k6 Cloud.
//   //
//   // cloud: {
//   //   // The ID of the project to which the test is assigned in the k6 Cloud UI.
//   //   // By default tests are executed in default project.
//   //   projectID: "",
//   //   // The name of the test in the k6 Cloud UI.
//   //   // Test runs with the same name will be grouped.
//   //   name: "script.js"
//   // },

//   // Uncomment this section to enable the use of Browser API in your tests.
//   //
//   // See https://grafana.com/docs/k6/latest/using-k6-browser/running-browser-tests/ to learn more
//   // about using Browser API in your test scripts.
//   //
//   // scenarios: {
//   //   // The scenario name appears in the result summary, tags, and so on.
//   //   // You can give the scenario any name, as long as each name in the script is unique.
//   //   ui: {
//   //     // Executor is a mandatory parameter for browser-based tests.
//   //     // Shared iterations in this case tells k6 to reuse VUs to execute iterations.
//   //     //
//   //     // See https://grafana.com/docs/k6/latest/using-k6/scenarios/executors/ for other executor types.
//   //     executor: 'shared-iterations',
//   //     options: {
//   //       browser: {
//   //         // This is a mandatory parameter that instructs k6 to launch and
//   //         // connect to a chromium-based browser, and use it to run UI-based
//   //         // tests.
//   //         type: 'chromium',
//   //       },
//   //     },
//   //   },
//   // }
// };

// The function that defines VU logic.
//
// See https://grafana.com/docs/k6/latest/examples/get-started-with-k6/ to learn more
// about authoring k6 scripts.
//
// const payload = JSON.stringify({
//   userName: 'admin',
//   password: 'admin',
// });

// export default function() {
//   const response = Http.post(
//     'http://dashboard.ulake.usth.edu.vn/api/grpc/grpc-login',
//     payload,
//     {
//       headers: {
//         'accept': '*/*',
//         'Content-Type': 'application/json',
//       },
//       tags: { name: 'grpc_login' }, // Add a tag for Grafana visualization
//     }
//   );

//   // Assertions for status code and response time (consider adding more)
//   response.status.shouldBe(200);
//   response.timing.duration.shouldBeLessThan(2000); // Adjust based on expected response time
// }



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
    'http://dashboard.ulake.usth.edu.vn/api/grpc/grpc-login',
    payload,
    {
      headers: {
        'accept': '*/*',
        'Content-Type': 'application/json',
      },
      tags: { name: 'grpc_login' }, // Add a tag for Grafana visualization
    }
  );
}