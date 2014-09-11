The aim of this plugin is to use XSLT to extract metadata from HTML DOM structures.

| Your Data | --> | Parse-html plugin | --> | DOM structure | --> |XSLT plugin |
                  | or TIKA plugin    |
                  
                  
The main advantage is that:
- You won't have to produce any java code, only XSLT and configuration
- It can process DOM structure from DocumentFragment (@see NekoHtml and @see TagSoup)
- It is HtmlParseFilter plugin compatible and can be plugged as any other plugin (parse-js, parse-swf, etc...)

