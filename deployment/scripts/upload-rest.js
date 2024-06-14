import http from 'k6/http';
import exec from 'k6/x/exec';

// export let options = {
//     stages: [
//       // Stage 1: Gradually ramp up to 10 VUs over 30s
//       { duration: '10s', target: 10 },
//       // Stage 2: Hold at 10 VUs for 60s
//       { duration: '20s', target: 10 },
//       // Stage 3: Gradually ramp down to 0 VUs over 30s
//       { duration: '10s', target: 0 },
//     ],
// };

export default function () {
    var result = exec('curl -F "file=@/Users/hoangphuc/Downloads/1.jpg" http://dashboard.ulake.usth.edu.vn/api/upload');
    console.log(result);
}