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
    Date.prototype.Format = function (fmt) { //author: meizz 
      var o = {
          "M+": this.getMonth() + 1, //月份 
          "d+": this.getDate(), //日 
          "h+": this.getHours(), //小时 
          "m+": this.getMinutes(), //分 
          "s+": this.getSeconds(), //秒 
          "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
          "S": this.getMilliseconds() //毫秒 
      };
      if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
      for (var k in o)
      if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
      return fmt;
    }



    startDate=new Date();
    startDate.setTime(startDate.getTime()-1000*60*60*24);
    endDate=new Date();
    $(".start_date_time").val(startDate.Format("yyyy-MM-dd hh:mm:ss"));
    $(".end_date_time").val(endDate.Format("yyyy-MM-dd hh:mm:ss"));
    $(".start_date_time").datetimepicker({language:  'zh-CN',format: 'yyyy-mm-dd hh:ii:ss',autoclose:true}).on('changeDate', function(ev){
        startDate=new Date(ev.date);
        draw(startDate,endDate);
      });
    $(".end_date_time").datetimepicker({language:  'zh-CN',format: 'yyyy-mm-dd hh:ii:ss',autoclose:true,date:new Date()}).on('changeDate', function(ev){
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