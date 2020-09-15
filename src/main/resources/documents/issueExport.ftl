<html lang="zh-CN">
<head>
  <style>
      th{border:solid 1px #acacac; text-align: center; padding: 5px}
      td{border:solid 1px #acacac; text-align: center; padding: 5px}
  </style>
</head>
  <body>
  <div>
    <table style="font-family: simhei; border:1px solid #000; width: 100%; border-collapse: collapse;">
      <thead>
      <tr>
        <th style="width: 5%">ID</th>
        <th style="width: 7.5%">提交人</th>
        <th style="width: 7.5%;">接收人</th>
        <th style="width: 10%;">模块</th>
        <th style="width: 5%;">严重性</th>
        <th style="width: 5%">SLA</th>
        <th style="width: 7%">解决状态</th>
        <th style="width: 9%">问题状态</th>
        <th style="width: 31%">概述</th>
        <th style="width: 6.5%">提交时间</th>
        <th style="width: 6.5%">更新时间</th>
      </tr>
      </thead>
      <tbody>
      <#list issueList as issue>
        <tr>
          <td>${issue.id}</td>
          <td>${issue.reportedBy ! '-'}</td>
          <td>${issue.assignedTo ! '-'}</td>
          <td>${issue.moduleName ! '-'}</td>
          <td>${issue.severity ! '-'}</td>
          <td>${issue.sla ! '-'}</td>
          <td>${issue.resolution ! '-'}</td>
          <td>${issue.status ! '-'}</td>
          <td>${issue.title ! '-'}</td>
          <td>${issue.reportedAt ! '-'}</td>
          <td>${issue.updatedAt ! '-'}</td>
        </tr>
      </#list>
      </tbody>
    </table>
  </div>
  </body>
</html>