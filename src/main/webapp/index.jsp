<html>
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Expires" content="-1" />
<script type="text/javascript"
  src="js/dygraph-combined.js"></script>
</head>
<body>
<div id="graphdiv"></div>
<script type="text/javascript">
  g = new Dygraph(

    // containing div
    document.getElementById("graphdiv"),'data',
    {
	    width: 960,
	    height: 480,
	    title: '消息队列',
	    xlabel: '时间',
        ylabel: '数量'
    }

  );
</script>
</body>
</html>