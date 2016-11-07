package ru.spbpu.ics.generator

import java.util.Map
import java.util.Map.Entry

class GeneratorHtml_2 {
	private final Map<String, ? extends Number > map_own
	private final Map<String, ? extends Number > map_ans
	
	new(Map<String, ? extends Number > map_own, Map<String, ? extends Number > map_ans){
		this.map_own = map_own
		this.map_ans = map_ans
	}
	
	def doGenerate(){
		'''
		<!DOCTYPE html>
		<html>
		  <head>
		    <link rel="stylesheet" href="http://cdn.jsdelivr.net/chartist.js/latest/chartist.min.css">
		    <script src="http://cdn.jsdelivr.net/chartist.js/latest/chartist.min.js"></script>
		  </head>
		  <body>
		  <style>
		  	div{
		  	 	height:600px;
		  		weight:400px;
		  	}
		  </style>
		  	<div class="ct-chart ct-perfect-fourth"></div>
		  	<style>
		  		.ct-series-a .ct-line,
		  		.ct-series-a .ct-point {
				fill-opacity: 0.2;
				}
				.ct-series-b .ct-line,
				.ct-series-b .ct-point {
					stroke: green;
					fill: green;
					fill-opacity: 0.2;
				}
		  	</style>
		  	<script type="text/javascript">
				var chart = new Chartist.Line('.ct-chart', {
				  «labelsAndSeries(map_own)»
				}, {
				  low: 0,
				  showArea: true,
				  showPoint: false,
				  fullWidth: true
				});
				
				chart.on('draw', function(data) {
				  if(data.type === 'line' || data.type === 'area') {
				    data.element.animate({
				      d: {
				        begin: 200 * data.index,
				        dur: 2000,
				        from: data.path.clone().scale(1, 0).translate(0, data.chartRect.height()).stringify(),
				        to: data.path.clone().stringify(),
				        easing: Chartist.Svg.Easing.easeOutQuint
				      }
				    });
				  }
				});
		  	</script>
		  </body>
		</html>
		'''
	}
	
	def labelsAndSeries(Map<String, ? extends Number> map){
		var labels = new StringBuilder()
		var series = new StringBuilder()
		
		labels.append("labels: [")
		series.append("series: [\n")
		series.append(map_own.values)
		series.append(",\n")
		series.append(map_ans.values)
		series.append(",\n")
		
		for(Entry<String, ? extends Number> entry: map.entrySet){
			labels.append("'" + entry.key + "', ")
		}
		labels.append("],\n")
		series.append("]")
		labels.append(series)
		
		return labels.toString
	}
}