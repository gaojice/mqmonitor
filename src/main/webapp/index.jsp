<html>
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
 <meta http-equiv="Pragma" content="no-cache" />
 <meta http-equiv="Expires" content="-1" />
 <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
 <link href="css/datetimepicker.css" rel="stylesheet" media="screen">
</head>
<body>
开始<input id="start_date_time" size="16" type="text"  readonly class="start_date_time">
结束<input id="end_date_time" size="16" type="text"  readonly class="end_date_time">

  <div id="graphdiv"></div>
  <script type="text/javascript" src="js/dygraph-combined.js"></script>
  <script type="text/javascript" src="js/jquery-1.8.3.min.js" charset="UTF-8"></script>
  <script type="text/javascript" src="js/bootstrap.min.js"></script>
  <script type="text/javascript" src="js/bootstrap-datetimepicker.js" charset="UTF-8"></script>
  <script type="text/javascript" src="js/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
  <script type="text/javascript">
    startDate=new Date();
    endDate=new Date();
    $(".start_date_time").datetimepicker({language:  'zh-CN',format: 'yyyy-mm-dd hh:ii',autoclose:true}).on('changeDate', function(ev){
        startDate=new Date(ev.date);
        draw(startDate,endDate);
      });
    $(".end_date_time").datetimepicker({language:  'zh-CN',format: 'yyyy-mm-dd hh:ii',autoclose:true,date:new Date()}).on('changeDate', function(ev){
        endDate=new Date(ev.date);
        draw(startDate,endDate);
      });

    draw();
    function draw(){
  
      g = new Dygraph(
        // containing div
        document.getElementById("graphdiv"),'data?start='+startDate.valueOf()+'&end='+endDate.valueOf(),
        {
           width: 960,
           height: 480,
           title: '消息队列',
           xlabel: '时间',
           ylabel: '数量'
       }

       );
    }
  </script>
</body>
</html>