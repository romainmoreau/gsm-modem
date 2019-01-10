var stompClient;

function init() {
	var webSocket = new SockJS(stompEndpointUrl);
	stompClient = Stomp.over(webSocket);
	stompClient.connect({}, function() {
		stompClient.subscribe('/topic/line', function(e) {
			$('table').append('<tr><td><pre>' + e.body + '</pre></td></tr>');
		});
		$('input').keyup(function(e) {
			if (e.keyCode == 13) {
				stompClient.send('/app/command', {}, $(this).val());
				$(this).val('');
			}
		});
	}, function(error) {
		alert(error);
	});
}

$(document).ready(init);
