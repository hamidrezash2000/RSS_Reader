[![RSS READER](http://uupload.ir/files/9fje_rss-icon.png)](https://github.com/hamidrezash2000/RSS_Reader) 
# RSS Reader &middot;  [![Build Status](https://api.travis-ci.com/hamidrezash2000/rss_reader.svg?branch=master)](https://api.travis-ci.com/hamidrezash2000/rss_reader) [![codecov](https://codecov.io/gh/hamidrezash2000/RSS_Reader/branch/master/graph/badge.svg)](https://codecov.io/gh/hamidrezash2000/RSS_Reader) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/2c8a8ce4f67b4e358b359c71fb801de7)](https://www.codacy.com/app/hamidrezash2000/RSS_Reader?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=hamidrezash2000/RSS_Reader&amp;utm_campaign=Badge_Grade)


#### RSS Reader Features :

  - Fetch and update RSS feed reports in parallel every 60 seconds. 
  - Extract main content of report from report link
  - ShamsiDate for reports
  - Search in reports by feed, title, description, publishedDate

#### Installation :

- Use maven to install the dependencies.
- Install in.nimbo.database using schema.sql
- Config in.nimbo.database.properties
- Start using it :)

#### How To Search :
Use search in commandline with these attributes and format :
- -title:[title]
- -description:[description]
- -feedId:[feedId]
- -pubDate:[dateLowerBound>dateUpperBound]

example : 
```
search -title:خبر -feedId:2 -pubdate:1398/04/15>1398/04/18
```
