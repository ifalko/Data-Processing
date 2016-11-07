package ru.spbpu.ics.generator


import java.util.Map
import java.util.Map.Entry

class GeneratorHtml_1 {
	private final Map<String, ? extends Number > map_frequency
	
	new(Map<String, ? extends Number > map_frequency){
		this.map_frequency = map_frequency
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
		  	<script type="text/javascript">
				new Chartist.Bar('.ct-chart', {
				  «labelsAndSeries(map_frequency)»
				}, {
				  distributeSeries: true
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
		series.append("series: ")
		series.append(map_frequency.values)
		
		for(Entry<String, ? extends Number> entry: map.entrySet){
			labels.append("'" + entry.key + "', ")
		}
		labels.append("],\n")
		labels.append(series)
		
		return labels.toString
	}
	
}