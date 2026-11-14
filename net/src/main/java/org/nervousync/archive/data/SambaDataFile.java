/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nervousync.archive.data;

import jcifs.CIFSContext;
import jcifs.CIFSException;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbRandomAccessFile;
import org.nervousync.archive.core.DataFile;
import org.nervousync.archive.enumerations.OpenMode;
import org.nervousync.utils.commons.StringUtils;
import org.nervousync.utils.net.EnhanceFileUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;

/**
 * <h2 class="en-US">Samba data file implement class</h2>
 * <h2 class="zh-CN">Samba数据文件实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Oct 26, 2026 09:37:28 $
 */
public final class SambaDataFile extends SmbRandomAccessFile implements DataFile {

	/**
	 * <span class="en-US">Current file path</span>
	 * <span class="zh-CN">当前文件地址</span>
	 */
	private final String filePath;
	/**
	 * <span class="en-US">Enumeration value of the open mode</span>
	 * <span class="zh-CN">打开模式枚举值</span>
	 */
	private final OpenMode openMode;
	/**
	 * <span class="en-US">Context</span>
	 * <span class="zh-CN">上下文</span>
	 */
	private final CIFSContext context;

	/**
	 * <h3 class="en-US">Constructor method for the samba data file implement class</h3>
	 * <h3 class="zh-CN">Samba数据文件实现类的构造方法</h3>
	 *
	 * @param smbLocation   <span class="en-US">File path</span>
	 *                      <span class="zh-CN">文件地址</span>
	 * @param mode          <span class="en-US">The open mode</span>
	 *                      <span class="zh-CN">打开模式</span>
	 * @param properties    <span class="en-US">Properties information</span>
	 *                      <span class="zh-CN">参数信息</span>
	 * @param authenticator <span class="en-US">The authenticator information</span>
	 *                      <span class="zh-CN">身份认证信息</span>
	 * @throws CIFSException         <span class="en-US">If CIFS properties has error</span>
	 *                               <span class="zh-CN">如果CIFS属性信息出现错误</span>
	 * @throws MalformedURLException <span class="en-US">If the samba location is invalid</span>
	 *                               <span class="zh-CN">如果文件地址错误</span>
	 */
	public SambaDataFile(final String smbLocation, final String mode, final Properties properties,
	                     final NtlmPasswordAuthenticator authenticator) throws CIFSException, MalformedURLException {
		this(smbLocation, mode, EnhanceFileUtils.generateContext(properties, authenticator));
	}

	/**
	 * <h3 class="en-US">Constructor method for the samba data file implement class</h3>
	 * <h3 class="zh-CN">Samba数据文件实现类的构造方法</h3>
	 *
	 * @param smbLocation <span class="en-US">File path</span>
	 *                    <span class="zh-CN">文件地址</span>
	 * @param mode        <span class="en-US">The open mode</span>
	 *                    <span class="zh-CN">打开模式</span>
	 * @param context     <span class="en-US">Context information</span>
	 *                    <span class="zh-CN">环境上下文</span>
	 * @throws MalformedURLException <span class="en-US">If the samba location is invalid</span>
	 *                               <span class="zh-CN">如果文件地址错误</span>
	 * @throws SmbException          <span class="en-US">If an error occurs when open samba file</span>
	 *                               <span class="zh-CN">打开文件出现错误</span>
	 */
	private SambaDataFile(final String smbLocation, final String mode, final CIFSContext context)
			throws MalformedURLException, SmbException {
		super(new SmbFile(smbLocation, context), mode);
		this.filePath = smbLocation;
		if (mode.equalsIgnoreCase("r")) {
			this.openMode = OpenMode.READ;
		} else {
			this.openMode = OpenMode.EDIT;
		}
		this.context = context;
	}

	@Override
	public String filePath() {
		return this.filePath;
	}

	@Override
	public OpenMode openMode() {
		return this.openMode;
	}

	@Override
	public List<String> splitFiles() throws IOException {
		return List.of();
	}

	@Override
	public DataFile newFile(final String filePath, final String mode) throws IOException {
		return new SambaDataFile(filePath, StringUtils.isEmpty(mode) ? this.currentMode() : mode, this.context);
	}
}
