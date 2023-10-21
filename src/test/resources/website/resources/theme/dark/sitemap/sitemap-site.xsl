    	<div id="sitemap">
			<h1>Sitemap - Site</h1>
			<table cellpadding="0" cellspacing="0">
				<tr>
					<th style="width: 60%;">Location</th>
					<th>Priority</th>
					<th>Change Frequency</th>
					<th>Last Modified</th>
				</tr>
				<xsl:for-each select="sitemap:urlset/sitemap:url">
					<tr>
						<xsl:if test="position() mod 2 != 0">
							<xsl:attribute name="class">odd</xsl:attribute>
						</xsl:if>
						<td>
							<xsl:variable name="itemURL">
								<xsl:value-of select="sitemap:loc" />
							</xsl:variable>
							<a href="{$itemURL}">
								<xsl:value-of select="sitemap:loc" />
							</a>
						</td>
						<td>
							<xsl:value-of select="sitemap:priority" />
						</td>
						<td>
							<xsl:value-of select="sitemap:changefreq" />
						</td>
						<td>
							<xsl:value-of
								select="concat(substring(sitemap:lastmod, 0, 11),concat(' ', substring(sitemap:lastmod, 12, 5)))" />
						</td>
					</tr>
				</xsl:for-each>
			</table>
    	</div><!-- sitemap -->
