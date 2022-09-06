#!/usr/bin/env python3

import logging
import requests
from bs4 import BeautifulSoup
import re
import json

import urllib3

urllib3.disable_warnings()


logging.basicConfig(
    format='%(asctime)s %(levelname)s:%(message)s',
    level=logging.INFO)


class Crawler:
    def __init__(self, url):
        self.url = url

    def download(self, url):
        return requests.get(url, verify=False).content

    def getInnerText(self, node):
        return u''.join(node.findAll(text=True))

    def parsePage(self, html):
        soup = BeautifulSoup(html, 'html.parser')
        page = {

        }
        links = []
        for table in soup.find_all('table'):
            for tr in table.find_all('tr'):
                td = tr.find_all('td')
                if td:
                    a = td[2].a
                    if a and a.has_attr('href'):
                        links.append(a['href'])
        page['links'] = links
        for a in soup.find_all('a'):
            if a and self.getInnerText(a).strip() == "Tiếp theo" and a.has_attr('href'):
                page['next'] = a['href']
        return page

    def extract(self, id, html):
        soup = BeautifulSoup(html, 'html.parser')
        info = {}
        for table in soup.find_all('table'):
            for tr in table.find_all('tr'):
                td = tr.find_all('td')
                if td:
                    key = td[0].span.contents[0]
                    value = self.getInnerText(td[1])
                    value = re.sub(r'\t|\n', '', value)
                    info[key] = value
        info['id'] = id
        return info

    def start(self):
        logging.info(f'Start crawling...')
        idx = 0
        ret = []
        while True:
            idx = idx + 1
            logging.info(f'PAGE {idx}: {self.url}')
            html = self.download(self.url)
            page = self.parsePage(html)
            for link in page['links']:
                id = re.findall(r"deTaiId=\d{4}", link)
                if not id:
                    id = link
                else:
                    try:
                        id = id[0]
                    except:
                        pass
                logging.info(f'Fetching and parsing project {id}')
                html = self.download(link)
                try:
                    info = self.extract(id, html)
                    ret.append(info)
                except:
                    logging.info("Cannot parse html")
                    pass
            if 'next' in page:
                self.url = page['next']
            else:
                break
            with open("projects.json", "w") as f:
                json.dump(ret, f, indent=4, ensure_ascii=False)

if __name__ == '__main__':
    #Crawler(urls=['https://vast.gov.vn/web/guest/nhiem-vu-khoa-hoc-uoc-nghiem-thu']).start()
    Crawler('https://sontg.net/list.html').start()