<!DOCTYPE html>
<html lang="ru">
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpRequestAttachment" -->
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

    <!-- Highlight.js -->
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/github.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/json.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/bash.min.js"></script>
    <script>
        hljs.highlightAll();
    </script>

    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif;
            font-size: 13px;
            color: #24292f;
        }
        h4 {
            margin: 16px 0 8px;
            border-bottom: 1px solid #d0d7de;
            padding-bottom: 4px;
        }
        .badge {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 6px;
            font-weight: 600;
            font-size: 12px;
            background: #0969da;
            color: #fff;
        }
        .method {
            background: #1a7f37;
        }
        .url {
            font-family: monospace;
            word-break: break-all;
        }
        pre {
            background: #f6f8fa;
            padding: 12px;
            border-radius: 6px;
            overflow-x: auto;
        }
        table {
            border-collapse: collapse;
            width: 100%;
            margin-top: 8px;
        }
        th, td {
            text-align: left;
            padding: 6px 8px;
            border-bottom: 1px solid #d0d7de;
            font-family: monospace;
            vertical-align: top;
        }
        th {
            background: #f6f8fa;
            font-weight: 600;
            width: 220px;
        }
        .empty {
            color: #6e7781;
            font-style: italic;
        }
    </style>
</head>

<body>

<h4>HTTP Request</h4>

<div>
    <span class="badge method">${data.method}</span>
    <span class="url">${data.url}</span>
</div>

<#-- Headers -->
<h4>Headers</h4>
<#if data.headers?has_content>
<table>
    <#list data.headers?keys as key>
        <tr>
            <th>${key}</th>
            <td>${data.headers[key]}</td>
        </tr>
    </#list>
</table>
<#else>
<div class="empty">No headers</div>
</#if>

<#-- Cookies -->
<h4>Cookies</h4>
<#if data.cookies?has_content>
<table>
    <#list data.cookies?keys as key>
        <tr>
            <th>${key}</th>
            <td>${data.cookies[key]}</td>
        </tr>
    </#list>
</table>
<#else>
<div class="empty">No cookies</div>
</#if>

<#-- Form params -->
<h4>Form parameters</h4>
<#if data.formParams?has_content>
<table>
    <#list data.formParams?keys as key>
        <tr>
            <th>${key}</th>
            <td>${data.formParams[key]}</td>
        </tr>
    </#list>
</table>
<#else>
<div class="empty">No form parameters</div>
</#if>

<#-- Body -->
<h4>Body</h4>
<#if data.body?has_content>
<pre><code class="language-json">${data.body}</code></pre>
<#else>
<div class="empty">Empty body</div>
</#if>

<#-- Curl -->
<h4>cURL</h4>
<#if data.curl?has_content>
<pre><code class="language-bash">${data.curl}</code></pre>
<#else>
<div class="empty">cURL is not available</div>
</#if>

</body>
</html>