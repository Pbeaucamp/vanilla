function gup(name) {
	name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regexS = "[\\?&]" + name + "=([^&#]*)";

	var regex = new RegExp(regexS);
	var results = regex.exec(window.location.href);

	return results == null ? null : results[1];
}

var locale = gup('locale');
if (locale == 'fr_FR') {
	document.getElementById('conteneur').className = 'fr';
} else {
	document.getElementById('conteneur').className = 'en';
}

(function(a) {
	a.fn
			.extend({
				reflect : function(b) {
					b = a.extend({
						height : 1 / 3,
						opacity : 0.5
					}, b);
					return this
							.unreflect()
							.each(
									function() {
										var c = this;
										if (/^img$/i.test(c.tagName)) {
											function d() {
												var g = c.width, f = c.height, l, i, m, h, k;
												i = Math
														.floor((b.height > 1) ? Math
																.min(
																		f,
																		b.height)
																: f * b.height);
												l = a("<canvas />")[0];
												if (l.getContext) {
													h = l.getContext("2d");
													try {
														a(l).attr({
															width : g,
															height : i
														});
														h.save();
														h.translate(0, f - 1);
														h.scale(1, -1);
														h.drawImage(c, 0, 0, g,
																f);
														h.restore();
														h.globalCompositeOperation = "destination-out";
														k = h
																.createLinearGradient(
																		0, 0,
																		0, i);
														k
																.addColorStop(
																		0,
																		"rgba(255, 255, 255, "
																				+ (1 - b.opacity)
																				+ ")");
														k
																.addColorStop(
																		1,
																		"rgba(255, 255, 255, 1.0)");
														h.fillStyle = k;
														h.rect(0, 0, g, i);
														h.fill()
													} catch (j) {
														return
													}
												} else {
													if (!window.ActiveXObject) {
														return
													}
													l = a("<img />")
															.attr("src", c.src)
															.css(
																	{
																		width : g,
																		height : f,
																		marginBottom : i
																				- f,
																		filter : "FlipV progid:DXImageTransform.Microsoft.Alpha(Opacity="
																				+ (b.opacity * 100)
																				+ ", FinishOpacity=0, Style=1, StartX=0, StartY=0, FinishX=0, FinishY="
																				+ (i
																						/ f * 100)
																				+ ")"
																	})[0]
												}
												a(l).css({
													display : "block",
													border : 0
												});
												m = a(
														/^a$/i
																.test(c.parentNode.tagName) ? "<span />"
																: "<div />")
														.insertAfter(c).append(
																[ c, l ])[0];
												m.className = c.className;
												a
														.data(
																c,
																"reflected",
																m.style.cssText = c.style.cssText);
												a(m).css({
													width : g,
													height : f + i,
													overflow : "hidden"
												});
												c.style.cssText = "display: block; border: 0px";
												c.className = "reflected"
											}
											if (c.complete) {
												d()
											} else {
												a(c).load(d)
											}
										}
									})
				},
				unreflect : function() {
					return this.unbind("load").each(function() {
						var c = this, b = a.data(this, "reflected"), d;
						if (b !== undefined) {
							d = c.parentNode;
							c.className = d.className;
							c.style.cssText = b;
							a.removeData(c, "reflected");
							d.parentNode.replaceChild(c, d)
						}
					})
				}
			})
})(jQuery);

// AUTOLOAD CODE BLOCK (MAY BE CHANGED OR REMOVED)
jQuery(function($) {
	$("img.reflect").reflect({/* Put custom options here */});
});

;
(function($) {
	//
	// Detect CSS transform support
	//
	var transform = (function() {
		var vendors = [ 'webkit', 'moz', 'ms' ];
		var style = document.createElement("div").style;
		var trans = 'transform' in style ? 'transform' : undefined;

		for (var i = 0, count = vendors.length; i < count; i++) {
			var prop = vendors[i] + 'Transform';
			if (prop in style) {
				trans = prop;
				break;
			}
		}

		return trans;
	})();

	var Item = function(element, options) {
		element.item = this;
		this.element = element;

		if (element.tagName === 'IMG') {
			this.fullWidth = element.width;
			this.fullHeight = element.height;
		} else {
			element.style.display = "inline-block";
			this.fullWidth = element.offsetWidth;
			this.fullHeight = element.offsetHeight;
		}

		element.style.position = 'absolute';

		if (options.mirror && this.element.tagName === 'IMG') {
			// Wrap image in a div together with its generated reflection
			this.reflection = $(element).reflect(options.mirror).next()[0];

			var $reflection = $(this.reflection);
			this.reflection.fullHeight = $reflection.height();
			$reflection.css('margin-top', options.mirror.gap + 'px');
			$reflection.css('width', '100%');
			element.style.width = "100%";

			// The item element now contains the image and reflection
			this.element = this.element.parentNode;
			this.element.item = this;
			this.element.alt = element.alt;
			this.element.title = element.title;
		}

		if (transform && options.transforms)
			this.element.style[transform + "Origin"] = "0 0";

		this.moveTo = function(x, y, scale) {
			this.width = this.fullWidth * scale;
			this.height = this.fullHeight * scale;
			this.x = x;
			this.y = y;
			this.scale = scale;

			var style = this.element.style;
			style.zIndex = "" + (scale * 100) | 0;

			if (transform && options.transforms) {
				style[transform] = "translate(" + x + "px, " + y + "px) scale("
						+ scale + ")";
			} else {
				// Manually resize the gap between the image and its reflection
				if (options.mirror && this.element.tagName === 'IMG')
					this.reflection.style.marginTop = (options.mirror.gap * scale)
							+ "px";

				style.width = this.width + "px";
				style.left = x + "px";
				style.top = y + "px";
			}
		}
	}

	var time = !window.performance || !window.performance.now ? function() {
		return +new Date()
	} : function() {
		return performance.now()
	};

	//
	// Detect requestAnimationFrame() support
	//
	// Support legacy browsers:
	// http://www.paulirish.com/2011/requestanimationframe-for-smart-animating/
	//
	var cancelFrame = window.cancelAnimationFrame
			|| window.cancelRequestAnimationFrame;
	var requestFrame = window.requestAnimationFrame;

	(function() {
		var vendors = [ 'webkit', 'moz', 'ms' ];

		for (var i = 0, count = vendors.length; i < count && !cancelFrame; i++) {
			cancelFrame = window[vendors[i] + 'CancelAnimationFrame']
					|| window[vendors[i] + 'CancelRequestAnimationFrame'];
			requestFrame = requestFrame
					&& window[vendors[i] + 'RequestAnimationFrame'];
		}
	}());

	var Carousel = function(element, options) {
		var self = this;
		var $container = $(element);
		this.items = [];
		this.xOrigin = (options.xOrigin === null) ? $container.width() * 0.5
				: options.xOrigin;
		this.yOrigin = (options.yOrigin === null) ? $container.height() * 0.1
				: options.yOrigin;
		this.xRadius = (options.xRadius === null) ? $container.width() / 2.3
				: options.xRadius;
		this.yRadius = (options.yRadius === null) ? $container.height() / 6
				: options.yRadius;
		this.farScale = options.farScale;
		this.rotation = this.destRotation = Math.PI / 2; // start with the
															// first item
															// positioned in
															// front
		this.speed = options.speed;
		this.smooth = options.smooth;
		this.fps = options.fps;
		this.timer = 0;
		this.autoPlayAmount = options.autoPlay;
		this.autoPlayDelay = options.autoPlayDelay;
		this.autoPlayTimer = 0;
		this.onLoaded = options.onLoaded;
		this.onRendered = options.onRendered;
		this.onAnimationFinished = options.onAnimationFinished;

		this.itemOptions = {
			transforms : options.transforms
		}

		if (options.mirror) {
			this.itemOptions.mirror = $.extend({
				gap : 2
			}, options.mirror);
		}

		$container.css({
			position : 'relative',
			overflow : 'hidden'
		});

		// Rotation:
		// * 0 : right
		// * Pi/2 : front
		// * Pi : left
		// * 3 Pi/2 : back
		this.renderItem = function(itemIndex, rotation) {
			var item = this.items[itemIndex];
			var sin = Math.sin(rotation);
			var farScale = this.farScale;
			var scale = farScale + ((1 - farScale) * (sin + 1) * 0.5);

			item
					.moveTo(
							this.xOrigin
									+ (scale * ((Math.cos(rotation) * this.xRadius) - (item.fullWidth * 0.5))),
							this.yOrigin + (scale * sin * this.yRadius), scale);
		}

		this.render = function() {
			var count = this.items.length;
			var spacing = 2 * Math.PI / count;
			var radians = this.rotation;

			for (var i = 0; i < count; i++) {
				this.renderItem(i, radians);
				radians += spacing;
			}

			if (typeof this.onRendered === 'function')
				this.onRendered(this);
		}

		this.playFrame = function() {
			var rem = self.destRotation - self.rotation;
			var now = time();
			var dt = (now - self.lastTime) * 0.002;
			self.lastTime = now;

			if (Math.abs(rem) < 0.003) {
				self.rotation = self.destRotation;
				self.pause();

				if (typeof self.onAnimationFinished === 'function')
					self.onAnimationFinished();
			} else {
				// Asymptotically approach the destination
				self.rotation = self.destRotation - rem
						/ (1 + (self.speed * dt));
				self.scheduleNextFrame();
			}

			self.render();
		}

		this.scheduleNextFrame = function() {
			this.lastTime = time();

			this.timer = this.smooth && cancelFrame ? requestFrame(self.playFrame)
					: setTimeout(self.playFrame, 1000 / this.fps);
		}

		this.itemsRotated = function() {
			return this.items.length * ((Math.PI / 2) - this.rotation)
					/ (2 * Math.PI);
		}

		this.floatIndex = function() {
			var count = this.items.length;
			var floatIndex = this.itemsRotated() % count;

			// Make sure float-index is positive
			return (floatIndex < 0) ? floatIndex + count : floatIndex;
		}

		this.nearestIndex = function() {
			return Math.round(this.floatIndex()) % this.items.length;
		}

		this.nearestItem = function() {
			return this.items[this.nearestIndex()];
		}

		this.play = function() {
			if (this.timer === 0)
				this.scheduleNextFrame();
		}

		this.pause = function() {
			this.smooth && cancelFrame ? cancelFrame(this.timer)
					: clearTimeout(this.timer);
			this.timer = 0;
		}

		//
		// Spin the carousel by (+-) count items
		//
		this.go = function(count) {
			this.destRotation += (2 * Math.PI / this.items.length) * count;
			this.play();
		}

		this.goTo = function(index) {
			var count = this.items.length;

			// Find the shortest way to rotate item to front
			var diff = index - (this.floatIndex() % count);

			if (2 * Math.abs(diff) > count)
				diff -= (diff > 0) ? count : -count;

			// Halt any rotation already in progress
			this.destRotation = this.rotation;

			// Spin the opposite way to bring item to front
			this.go(-diff);

			// Return rotational distance (in items) to the target
			return diff;
		}

		this.deactivate = function() {
			this.pause();
			clearInterval(this.autoPlayTimer);
			if (options.buttonLeft)
				options.buttonLeft.unbind('click');
			if (options.buttonRight)
				options.buttonRight.unbind('click');
			$container.unbind('.cloud9');
		}

		this.autoPlay = function() {
			this.autoPlayTimer = setInterval(function() {
				self.go(self.autoPlayAmount)
			}, this.autoPlayDelay);
		}

		this.enableAutoPlay = function() {
			// Stop auto-play on mouse over
			$container.bind('mouseover.cloud9', function() {
				clearInterval(self.autoPlayTimer);
			});

			// Resume auto-play when mouse leaves the container
			$container.bind('mouseout.cloud9', function() {
				self.autoPlay();
			});

			this.autoPlay();
		}

		this.bindControls = function() {
			if (options.buttonLeft) {
				options.buttonLeft.bind('click', function() {
					self.go(-1);
					return false;
				});
			}

			if (options.buttonRight) {
				options.buttonRight.bind('click', function() {
					self.go(1);
					return false;
				});
			}

			if (options.mouseWheel) {
				$container.bind('mousewheel.cloud9', function(event, delta) {
					self.go((delta > 0) ? 1 : -1);
					return false;
				});
			}

			if (options.bringToFront) {
				$container.bind('click.cloud9',
						function(event) {
							var hits = $(event.target).closest(
									'.' + options.itemClass);

							if (hits.length !== 0) {
								var diff = self.goTo(self.items
										.indexOf(hits[0].item));

								// Suppress default browser action if the item
								// isn't roughly in front
								if (Math.abs(diff) > 0.5)
									event.preventDefault();
							}
						});
			}
		}

		var items = $container.find('.' + options.itemClass);

		this.finishInit = function() {
			//
			// Wait until all images have completely loaded
			//
			for (var i = 0; i < items.length; i++) {
				var item = items[i];
				if ((item.tagName === 'IMG')
						&& ((item.width === undefined) || ((item.complete !== undefined) && !item.complete)))
					return;
			}

			clearInterval(this.initTimer);

			// Init items
			for (i = 0; i < items.length; i++)
				this.items.push(new Item(items[i], this.itemOptions));

			// Disable click-dragging of items
			$container.bind('mousedown onselectstart', function() {
				return false
			});

			if (this.autoPlayAmount !== 0)
				this.enableAutoPlay();
			this.bindControls();
			this.render();

			if (typeof this.onLoaded === 'function')
				this.onLoaded(this);
		};

		this.initTimer = setInterval(function() {
			self.finishInit()
		}, 50);
	}

	//
	// The jQuery plugin
	//
	$.fn.Cloud9Carousel = function(options) {
		return this.each(function() {
			/* For full list of options see the README */
			options = $.extend({
				xOrigin : null, // null: calculated automatically
				yOrigin : null,
				xRadius : null,
				yRadius : null,
				farScale : 0.5, // scale of the farthest item
				transforms : true, // enable CSS transforms
				smooth : true, // enable smooth animation via
								// requestAnimationFrame()
				fps : 30, // fixed frames per second (if smooth animation is
							// off)
				speed : 4, // positive number
				autoPlay : 0, // [ 0: off | number of items (integer
								// recommended, positive is clockwise) ]
				autoPlayDelay : 4000,
				bringToFront : false,
				itemClass : 'cloud9-item',
				handle : 'carousel'
			}, options);

			$(this).data(options.handle, new Carousel(this, options));
		});
	}
})(window.jQuery || window.Zepto);

$("#carousel").Cloud9Carousel({
	buttonLeft : $("#buttons > .left"),
	buttonRight : $("#buttons > .right"),
	autoPlay : 0,
	bringToFront : true,
	mirror : {
		gap : 0,
		height : 0.35,
		opacity : 0.4
	}
});