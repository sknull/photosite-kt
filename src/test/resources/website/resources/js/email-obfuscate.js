rnd.today = new Date()
rnd.seed = rnd.today.getTime()

function rnd() {
	rnd.seed = (rnd.seed * 9301 + 49297) % 233280
	return rnd.seed / (233280.0);
};

function rand(number) {
	return Math.ceil(rnd() * number);
};

function munge(address, linktext) {
	address = address.toLowerCase()
	coded = ""

	linktext = (document.mungeForm.linkInput.value.length == 0 ? linktext = "\"+link+\"" : linktext = document.mungeForm.linkInput.value)

	unmixedkey = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
	inprogresskey = unmixedkey
	mixedkey = ""
	unshuffled = 62
	for (i = 0; i <= 62; i++) {
		ranpos = rand(unshuffled) - 1
		nextchar = inprogresskey.charAt(ranpos)
		mixedkey += nextchar
		before = inprogresskey.substring(0, ranpos)
		after = inprogresskey.substring(ranpos + 1, unshuffled)
		inprogresskey = before + after
		unshuffled -= 1
	}
	cipher = mixedkey

	shift = address.length

	txt = '<script type=\"text/javascript\" language=\"javascript\">\n'
			+ '<!-'
			+ '-\n'
			+ '// Email obfuscator script 2.1 by Tim Williams, University of Arizona\n'
			+ '// Random encryption key feature coded by Andrew Moulden\n'
			+ '// This code is freeware provided these four comment lines remain intact\n'
			+ '// A wizard to generate this code is at http://www.jottings.com/obfuscator/\n'

	for (j = 0; j < address.length; j++) {
		if (cipher.indexOf(address.charAt(j)) == -1) {
			chr = address.charAt(j)
			coded += address.charAt(j)
		} else {
			chr = (cipher.indexOf(address.charAt(j)) + shift) % cipher.length
			coded += cipher.charAt(chr)
		}
	}

	txt += '{ coded = \"'
			+ coded
			+ '\"\n'
			+ '  key = "'
			+ cipher
			+ '"\n'
			+ '  shift=coded.length\n'
			+ '  link=""\n'
			+ '  for (i=0; i<coded.length; i++) {\n'
			+ '    if (key.indexOf(coded.charAt(i))==-1) {\n'
			+ '      ltr = coded.charAt(i)\n'
			+ '      link += (ltr)\n'
			+ '    }\n'
			+ '    else {     \n'
			+ '      ltr = (key.indexOf(coded.charAt(i))-shift+key.length) % key.length\n'
			+ '      link += (key.charAt(ltr))\n' + '    }\n' + '  }\n'
			+ 'document.write("<a href=\'mailto:"+link+"\'>' + linktext
			+ '</a>")\n' + '}\n' + '//-' + '->\n' + '<'
			+ '/script><noscript>Sorry, you need Javascript on to email me.'
			+ '<' + '/noscript>\n'
	document.output.source.value = txt
}
