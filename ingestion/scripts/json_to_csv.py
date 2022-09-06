#!/usr/bin/env python3

import pandas as pd
with open('../../projects.json', encoding='utf-8') as f:
    df = pd.read_json(f)

df.to_csv('projects.csv', encoding='utf-8', index=False)