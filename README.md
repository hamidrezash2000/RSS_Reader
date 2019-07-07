[![RSS READER](http://uupload.ir/files/9fje_rss-icon.png)](https://github.com/hamidrezash2000/RSS_Reader) 
# RSS Reader &middot;  [![Build Status](https://api.travis-ci.com/hamidrezash2000/rss_reader.svg?branch=master)](https://api.travis-ci.com/hamidrezash2000/rss_reader) [![codecov](https://codecov.io/gh/hamidrezash2000/RSS_Reader/branch/master/graph/badge.svg)](https://codecov.io/gh/hamidrezash2000/RSS_Reader)


#### RSS Reader Features :

  - Fetch and update RSS feed reports in parallel every 60 seconds. 
  - Extract main content of report from report link
  - Search in reports by feed, title, description, publishedDate

#### Installation :

- Use maven to install the dependencies.
- Install database using schema.sql
- Config database.properties
- Start using it :)

#### How To Search :
Use search in commandline with these attributes and format :
- -title:[title]
- -description:[description]
- -feedId:[feedId]
- -pubDate:[dateLowerBound>dateUpperBound]

example : 
```
search -title:خبر -feedId:2 -pubDate:2000/02/23>2002/05/03
```