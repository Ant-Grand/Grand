document.addEventListener('DOMContentLoaded', function() {
    // Doxia loses img tag :-(
    grandModal.innerHTML += '<img class="grandcontent" id="grandImg"/><div class="grandcaption" id="grandCaption"></div>';

    // When an image is clicked, make the modal visible
    var modalImages = document.getElementsByClassName("modal-image");
    [].forEach.call(modalImages, function(item, idx) {
	item.addEventListener('click', function() {
	    document.getElementById("grandImg").src = this.src;
	    document.getElementById("grandCaption").innerHTML = this.alt;
	    grandModal.style.display = "block";
	});
    })

    // When <span> (x) is clicked, close the modal
    document.getElementById("grandClose").addEventListener('click', function() {
	grandModal.style.display = "none";
    });
}, false);