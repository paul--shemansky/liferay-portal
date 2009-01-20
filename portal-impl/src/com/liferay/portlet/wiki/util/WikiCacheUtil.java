/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portlet.wiki.util;

import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portlet.wiki.PageContentException;
import com.liferay.portlet.wiki.model.WikiPage;
import com.liferay.portlet.wiki.model.WikiPageDisplay;
import com.liferay.portlet.wiki.service.WikiPageLocalServiceUtil;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletURL;

import org.apache.commons.lang.time.StopWatch;

/**
 * <a href="WikiCacheUtil.java.html"><b><i>View Source</i></b></a>
 *
 * @author Jorge Ferrer
 *
 */
public class WikiCacheUtil {

	public static final String CACHE_NAME = WikiCacheUtil.class.getName();

	public static void clearCache(long nodeId) {
		for (String groupKey : _groups.keySet()) {
			if (groupKey.startsWith(CACHE_NAME + StringPool.POUND + nodeId)) {
				MultiVMPoolUtil.clearGroup(_groups, groupKey, _cache);
			}
		}
	}

	public static void clearCache(long nodeId, String title) {
		String groupKey = _encodeGroupKey(nodeId, title);

		MultiVMPoolUtil.clearGroup(_groups, groupKey, _cache);
	}

	public static WikiPageDisplay getDisplay(
		long nodeId, String title, PortletURL viewPageURL,
		PortletURL editPageURL, String attachmentURLPrefix) {

		StopWatch stopWatch = null;

		if (_log.isDebugEnabled()) {
			stopWatch = new StopWatch();

			stopWatch.start();
		}

		String key = _encodeKey(nodeId, title, viewPageURL.toString());

		String groupKey = _encodeGroupKey(nodeId, title);

		WikiPageDisplay pageDisplay = (WikiPageDisplay)MultiVMPoolUtil.get(
			_cache, key);

		if (pageDisplay == null) {
			pageDisplay = _getPageDisplay(
				nodeId, title, viewPageURL, editPageURL, attachmentURLPrefix);

			MultiVMPoolUtil.put(_cache, key, _groups, groupKey, pageDisplay);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"getDisplay for {" + nodeId + ", " + title + ", " +
					viewPageURL + ", " + editPageURL + "} takes " +
						stopWatch.getTime() + " ms");
		}

		return pageDisplay;
	}

	public static Map<String, Boolean> getOutgoingLinks(WikiPage page)
		throws PageContentException {

		String key = _encodeKey(
			page.getNodeId(), page.getTitle(), _OUTGOING_LINKS);

		String groupKey = _encodeGroupKey(page.getNodeId(), page.getTitle());

		Map<String, Boolean> links = (Map<String, Boolean>)MultiVMPoolUtil.get(
			_cache, key);

		if (links == null) {
			links = WikiUtil.getLinks(page);

			MultiVMPoolUtil.put(_cache, key, _groups, groupKey, links);
		}

		return links;
	}

	private static String _encodeGroupKey(long nodeId, String title) {
		return _encodeKey(nodeId, title, null);
	}

	private static String _encodeKey(
		long nodeId, String title, String postfix) {

		StringBuilder sb = new StringBuilder();

		sb.append(CACHE_NAME);
		sb.append(StringPool.POUND);
		sb.append(nodeId);
		sb.append(title);

		if (postfix != null) {
			sb.append(StringPool.POUND);
			sb.append(postfix);
		}

		return sb.toString();
	}

	private static WikiPageDisplay _getPageDisplay(
		long nodeId, String title, PortletURL viewPageURL,
		PortletURL editPageURL, String attachmentURLPrefix) {

		try {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Get page display for {" + nodeId + ", " + title + ", " +
						viewPageURL + ", " + editPageURL + "}");
			}

			return WikiPageLocalServiceUtil.getPageDisplay(
				nodeId, title, viewPageURL, editPageURL, attachmentURLPrefix);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get page display for {" + nodeId + ", " + title +
						", " + viewPageURL + ", " + editPageURL + "}");
			}

			return null;
		}
	}

	private static final String _OUTGOING_LINKS = "OUTGOING_LINKS";

	private static Log _log = LogFactoryUtil.getLog(WikiUtil.class);

	private static PortalCache _cache = MultiVMPoolUtil.getCache(CACHE_NAME);

	private static Map<String, Set<String>> _groups =
		new ConcurrentHashMap<String, Set<String>>();

}