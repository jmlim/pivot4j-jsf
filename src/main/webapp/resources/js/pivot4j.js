function initializeAxisItems() {
	jQuery("#axis-config-panel div.axis-item").draggable({
		start : startDragAxisItem,
		stop : stopDragAxisItem
	}).each(initializeAxisItem);
}

function initializeAxisItem(i, item) {
	var query = jQuery(item);
	var offset = query.offset();

	item.originLeft = offset.left - query.parent().offset().left;

	query.css("position", "static").css("left", item.originLeft + "px");
}

function startDragAxisItem(e, ui) {
}

function stopDragAxisItem(e, ui) {
	ui.helper.css("position", "static").css("top", "0").css("left",
			ui.helper.get(0).originLeft + "px");
}