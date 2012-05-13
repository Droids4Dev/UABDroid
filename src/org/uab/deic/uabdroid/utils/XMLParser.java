/*
   Copyright 2012 Ruben Serrano, Joan Fuentes

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.uab.deic.uabdroid.utils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import org.uab.deic.uabdroid.AppPreferencesActivity;
import org.uab.deic.uabdroid.adapters.DatabaseAdapter;
import org.xml.sax.Attributes;

import android.content.Context;
import android.content.SharedPreferences;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.sax.TextElementListener;
import android.util.Log;
import android.util.Xml;

/**
 * 
 * @author Ruben Serrano
 *
 */

public class XMLParser 
{	
	public static final String FILE_SERVER_URL = "http://deic.uab.cat/~rserrano/android/app/";
	
	public static final String UPDATE_FILE = "update.xml";
	public static final String FONTS_FILE = "fonts.xml";
	public static final String MATERIALS_FILE = "materials.xml";
	public static final String INFO_FILE = "info.xml";
	public static final String SESSIONS_FILE = "sessions.xml";
	public static final String TEST_FILE = "test.xml";
	
	public static void update(Context _context)
	{
		try 
		{
			URL url = new URL(FILE_SERVER_URL + UPDATE_FILE);
			parseUpdateXML(url.openConnection().getInputStream(), _context, false);
		} 
		catch (Exception e) 
		{
			Log.v("Exception", "URL opening error: " + UPDATE_FILE);
		}		
	}
	
	public static boolean parseUpdateXML(InputStream _inputStream, final Context _context, final Boolean _insert)
	{	
		final SharedPreferences sharedPreferences = _context.getSharedPreferences(AppPreferencesActivity.INIT_PREFS,Context.MODE_PRIVATE);
		final DatabaseAdapter databaseAdapter = new DatabaseAdapter(_context);
		
		databaseAdapter.open();
		
		try 
		{
			RootElement root = new RootElement("actualitza");
			
			root.getChild("dades").setStartElementListener(new StartElementListener()
			{
				public void start(Attributes attrs) 
				{
					if (_insert)
					{
				        SharedPreferences.Editor editor = sharedPreferences.edit();
				        String tipus = attrs.getValue("tipus");
				        String max = attrs.getValue("max_v");
				        editor.putFloat(tipus, Float.valueOf(max));
				        editor.commit();
					}
					else
					{
						if (Float.valueOf(attrs.getValue("max_v")) > 
							sharedPreferences.getFloat(attrs.getValue("tipus"), 1.0f))
						{	
							if (attrs.getValue("tipus").equalsIgnoreCase(INFO_FILE))
							{
								try 
								{
									URL url = new URL(FILE_SERVER_URL + INFO_FILE);
									updateInfoXML(url.openConnection().getInputStream(), _context);
								} 
								catch (Exception e) 
								{
									Log.v("Exception", "URL opening error: " + INFO_FILE);
								}
							}
							else if (attrs.getValue("tipus").equalsIgnoreCase(MATERIALS_FILE))
							{
								try 
								{
									URL url = new URL(FILE_SERVER_URL + MATERIALS_FILE);
									parseMaterialsXML(url.openConnection().getInputStream(), databaseAdapter, false);
								} 
								catch (Exception e) 
								{
									Log.v("Exception", "URL opening error: " + MATERIALS_FILE);
								}
							}
							else if (attrs.getValue("tipus").equalsIgnoreCase(FONTS_FILE))
							{
								try 
								{
									URL url = new URL(FILE_SERVER_URL + FONTS_FILE);
									parseFontsXML(url.openConnection().getInputStream(), databaseAdapter, false);
								} 
								catch (Exception e) 
								{
									Log.v("Exception", "URL opening error: " + FONTS_FILE);
								}
							}
							else if (attrs.getValue("tipus").equalsIgnoreCase(SESSIONS_FILE))
							{
								try 
								{
									URL url = new URL(FILE_SERVER_URL + SESSIONS_FILE);
									parseSessionsXML(url.openConnection().getInputStream(), databaseAdapter, false);
								} 
								catch (Exception e) 
								{
									Log.v("Exception", "URL opening error: " + SESSIONS_FILE);
								}
							}
							else if (attrs.getValue("tipus").equalsIgnoreCase(TEST_FILE))
							{
								try 
								{
									URL url = new URL(FILE_SERVER_URL + TEST_FILE);
									parseTestXML(url.openConnection().getInputStream(), databaseAdapter, false);
								} 
								catch (Exception e) 
								{
									Log.v("Exception", "URL opening error: " + TEST_FILE);
								}
							}
							
							SharedPreferences.Editor editor = sharedPreferences.edit();
					        editor.putFloat(attrs.getValue("tipus"), Float.valueOf(attrs.getValue("max_v")));
					        editor.commit();
					        editor = null;
						}
					}
				}
			});
			
			Xml.parse(_inputStream, Xml.Encoding.UTF_8, root.getContentHandler());
		}
		catch (Exception _exception)
		{
		    Log.v("Exception", "Parsing error: " + UPDATE_FILE);
		    databaseAdapter.close();
		    return false;
		}
		databaseAdapter.close();
		
		return true;
	}
	
	public static void updateInfoXML(InputStream _inputStream, Context _context)
	{
		try {
			_context.deleteFile(INFO_FILE);
			FileOutputStream fos = _context.openFileOutput(INFO_FILE, Context.MODE_PRIVATE);
			int c = 0;
			while ((c = _inputStream.read())!=-1)
			{
				fos.write(c);
			}
		} 
		catch (Exception _exception)
		{
			Log.v("Exception", "Updating error: " + INFO_FILE);
		}
	}
	
	public static boolean parseInfoXML(InputStream _inputStream, final GeneralInfo _info)
	{
		final String locale = Locale.getDefault().toString();
		
		try 
		{
			RootElement root = new RootElement("info");
			Element item = root.getChild("versio");
			
			item.setStartElementListener(new StartElementListener()
			{
				public void start(Attributes attrs) 
				{
					String lang = attrs.getValue("lang"); 
					
					if (!_info.isFound())
					{
						_info.setFound(lang.compareToIgnoreCase(locale)==0);
						if ((_info.isFound())||(lang.compareToIgnoreCase("es_ES")==0))
						{
							_info.clear();
							_info.setSelected(true);
						}
					}
				}
			});
		 
			item.setEndElementListener(new EndElementListener()
			{
				public void end() 
				{
					if (_info.isSelected())
					{
						_info.setSelected(false);
					}
				}
			});
		 
			item.getChild("general").setEndTextElementListener(new EndTextElementListener()
			{
				public void end(String body) 
				{
					if (_info.isSelected())
					{
						_info.setDescription(body);
					}
				}
			});
			
			item = item.getChild("faqs").getChild("pregunta");
			
			item.getChild("p").setEndTextElementListener(new EndTextElementListener()
			{
				public void end(String body) 
				{
					if (_info.isSelected())
					{
						_info.addQuestion(body);
					}
				}
			});
			
			item.getChild("r").setEndTextElementListener(new EndTextElementListener()
			{
				public void end(String body) 
				{
					if (_info.isSelected())
					{
						_info.addAnswer(body);
					}
				}
			});
	        
			Xml.parse( _inputStream, Xml.Encoding.UTF_8, root.getContentHandler());
		}
		catch (Exception _exception)
		{
		    Log.v("Exception", "Parsing error: " + INFO_FILE);
		    return false;
		}
		return true;
	}
	
	public static boolean parseMaterialsXML(InputStream _inputStream, final DatabaseAdapter _databaseAdapter, final Boolean _insert)
	{	
		final Material material = new Material();
		
		try 
		{
			RootElement root = new RootElement("materials");
			Element item = root.getChild("material");
			
			item.setStartElementListener(new StartElementListener()
			{
				public void start(Attributes attrs) 
				{
					material.clear();
					
					String type = attrs.getValue("tipus");
					
					if (type.compareToIgnoreCase("transparencies")==0)
					{
						material.setType(Material.PRESENTATION_TYPE);
					}
					else if (type.compareToIgnoreCase("codi")==0)
					{
						material.setType(Material.CODE_TYPE);
					}
					else if (type.compareToIgnoreCase("enllas")==0)
					{
						material.setType(Material.LINK_TYPE);
					}
					
					material.setIndex(Integer.parseInt(attrs.getValue("index")));
				}
			});
		 
			item.setEndElementListener(new EndElementListener()
			{
				public void end() 
				{
					// llamada la BBDD
					
					if (_insert)
					{
						_databaseAdapter.addMaterial(material.getIndex(), material.getType(), material.getName("ca_ES"), 
											  material.getName("es_ES"), material.getUrl());
					}
					else
					{
						_databaseAdapter.updateMaterial(material.getIndex(), material.getType(), 
												 material.getName("ca_ES"), material.getName("es_ES"), 
												 material.getUrl());
					}
				}
			});
		 
			item.getChild("nom").setTextElementListener(new TextElementListener()
			{
				String lang;
				
				@Override
				public void start(Attributes attributes) 
				{
					lang = attributes.getValue("lang");
				}

				@Override
				public void end(String body) 
				{
					material.addName(lang, body);
				}
			});
			
			item.getChild("url").setEndTextElementListener(new EndTextElementListener()
			{
				public void end(String body) 
				{
					material.setUrl(body);
				}
			});
	        
			Xml.parse(_inputStream, Xml.Encoding.UTF_8, root.getContentHandler());
		}
		catch (Exception ex)
		{
		    Log.v("Exception", "Parsing error: " + MATERIALS_FILE);
		    return false;
		}
		return true;
	}
	
	public static boolean parseFontsXML(InputStream _inputStream, final DatabaseAdapter _databaseAdapter, final Boolean _insert)
	{
		final Source source = new Source();
		
		try 
		{
			RootElement root = new RootElement("fonts");
			Element item = root.getChild("font");
			
			item.setStartElementListener(new StartElementListener()
			{
				public void start(Attributes attrs) 
				{
					source.clear();
					
					String type = attrs.getValue("tipus");
					
					if (type.compareToIgnoreCase("llibre")==0)
					{
						source.setType(Source.BOOK_TYPE);
					}
					else if (type.compareToIgnoreCase("guia")==0)
					{
						source.setType(Source.GUIDE_TYPE);
					}
					
					source.setIndex(Integer.parseInt(attrs.getValue("index")));
				}
			});
		 
			item.setEndElementListener(new EndElementListener()
			{
				public void end() 
				{
					// llamada la BBDD
					
					if (_insert)
					{
						_databaseAdapter.addSource(source.getIndex(), 
												   source.getName("ca_ES"), 
												   source.getName("es_ES"), 
												   source.getDescription("ca_ES"), 
												   source.getDescription("es_ES"), 
												   source.getType(), 
												   source.getUrl());
					}
					else
					{
						_databaseAdapter.updateSource(source.getIndex(), 
								                      source.getName("ca_ES"), 
								                      source.getName("es_ES"), 
								                      source.getDescription("ca_ES"), 
								                      source.getDescription("es_ES"), 
								                      source.getType(), 
								                      source.getUrl());				
					}
				}
			});
		 
			item.getChild("nom").setTextElementListener(new TextElementListener()
			{
				String lang;
				
				@Override
				public void start(Attributes attributes) 
				{
					lang = attributes.getValue("lang");
				}

				@Override
				public void end(String body) 
				{
					source.addName(lang, body);
				}
			});
			
			item.getChild("descripcio").setTextElementListener(new TextElementListener()
			{
				String lang;
				
				@Override
				public void start(Attributes attributes) 
				{
					lang = attributes.getValue("lang");
				}

				@Override
				public void end(String body) 
				{
					source.addDescription(lang, body);
				}
			});
			
			item.getChild("url").setEndTextElementListener(new EndTextElementListener()
			{
				public void end(String body) 
				{
					source.setUrl(body);
				}
			});
	        
			Xml.parse(_inputStream, Xml.Encoding.UTF_8, root.getContentHandler());
		}
		catch (Exception ex)
		{
		    Log.v("Exception", "Parsing error: " + FONTS_FILE);
		    return false;
		}
		return true;
	}
	
	public static boolean parseSessionsXML(InputStream _inputStream, final DatabaseAdapter _databaseAdapter, final Boolean _insert)
	{
		final Session session = new Session();
		
		try 
		{
			RootElement root = new RootElement("sessions");
			Element item = root.getChild("sessio");
			
			item.setStartElementListener(new StartElementListener()
			{
				public void start(Attributes attrs) 
				{
					session.clear();
					session.setIndex(Integer.parseInt(attrs.getValue("index")));
				}
			});
		 
			item.setEndElementListener(new EndElementListener()
			{
				public void end() 
				{
					// llamada la BBDD
					int i;
					int index = session.getIndex();
					int size;
					
					if (_insert)
					{
						_databaseAdapter.addSession(index, session.getTitle("ca_ES"), 
											 session.getTitle("es_ES"), session.getRoom(), session.getDay(), 
											 session.getHour(), session.getContent("ca_ES"), 
											 session.getContent("es_ES"));
						
						size = session.getMaterialsSize();
						for(i = 0; i < size;  i++)
						{
							_databaseAdapter.addMaterialBySession(session.getMaterial(i), index);
						}
						
						size = session.getSourcesSize();
						for(i = 0; i < size;  i++)
						{
							_databaseAdapter.addSourceBySession(session.getSource(i), index);
						}
					}
					else
					{
						_databaseAdapter.updateSession(session.getIndex(), 
								                       session.getTitle("ca_ES"), 
								                       session.getTitle("es_ES"), 
								                       session.getRoom(), 
								                       session.getDay(), 
								                       session.getHour(), 
								                       session.getContent("ca_ES"), 
								                       session.getContent("es_ES"));
						
						_databaseAdapter.deleteMaterialsBySession(session.getIndex());
						
						size = session.getMaterialsSize();
						for(i = 0; i < size;  i++)
						{
							_databaseAdapter.addMaterialBySession(session.getMaterial(i), session.getIndex());
						}
						
						_databaseAdapter.deleteSourcesBySession(session.getIndex());
						
						size = session.getSourcesSize();
						for(i = 0; i < size;  i++) 
						{
							_databaseAdapter.addSourceBySession(session.getSource(i), session.getIndex());
						}
					}
				}
			});
		 
			item.getChild("titol").setTextElementListener(new TextElementListener()
			{
				String lang;
				
				@Override
				public void start(Attributes attributes) 
				{
					lang = attributes.getValue("lang");
				}

				@Override
				public void end(String body) 
				{
					session.addTitle(lang, body);
				}
			});
			
			item.getChild("aula").setEndTextElementListener(new EndTextElementListener()
			{
				@Override
				public void end(String body) 
				{
					session.setRoom(body);
				}
				
			});
			
			item.getChild("data").setEndTextElementListener(new EndTextElementListener()
			{
				@Override
				public void end(String body) 
				{
					session.setDay(body);
				}
				
			});
			
			item.getChild("hora").setEndTextElementListener(new EndTextElementListener()
			{
				@Override
				public void end(String body) 
				{
					session.setHour(body);
				}
				
			});
			
			item.getChild("contingut").setTextElementListener(new TextElementListener()
			{
				String lang;
				
				@Override
				public void start(Attributes attributes) 
				{
					lang = attributes.getValue("lang");
				}

				@Override
				public void end(String body) 
				{
					session.addContent(lang, body);
				}
			});

			item.getChild("materials").getChild("material").setStartElementListener(new StartElementListener()
			{
				@Override
				public void start(Attributes attrs) {
					session.addMaterial(Integer.parseInt(attrs.getValue("index")));
				}
			});
			
			item.getChild("fonts").getChild("font").setStartElementListener(new StartElementListener()
			{
				@Override
				public void start(Attributes attrs) {
					session.addSource(Integer.parseInt(attrs.getValue("index")));
				}
			});
	        
			Xml.parse( _inputStream, Xml.Encoding.UTF_8, root.getContentHandler());
		}
		catch (Exception ex)
		{
		    Log.v("Exception", "Parsing error: " + SESSIONS_FILE);
		    return false;
		}
		return true;
	}
	
	public static boolean parseTestXML(InputStream _inputStream, final DatabaseAdapter _databaseAdapter, final Boolean _insert)
	{
		final Examination exam = new Examination();
		
		try 
		{
			RootElement root = new RootElement("test");
			Element item = root.getChild("pregunta");
			
			item.setStartElementListener(new StartElementListener()
			{
				public void start(Attributes attrs) 
				{
					exam.clear();
					exam.setIndex(Integer.parseInt(attrs.getValue("index")));
				}
			});
		 
			item.setEndElementListener(new EndElementListener()
			{
				public void end() 
				{
					// llamada la BBDD
					
					if (_insert)
					{
						_databaseAdapter.addTestQuestion(exam.getIndex(), 
														 exam.getQuestion("ca_ES"), 
														 exam.getQuestion("es_ES"), 
														 exam.getAnswer(0),
														 exam.getAnswer(1),
														 exam.getAnswer(2));
					}
					else
					{
						_databaseAdapter.updateTestQuestion(exam.getIndex(), 
															exam.getQuestion("ca_ES"), 
															exam.getQuestion("es_ES"), 
															exam.getAnswer(0),
															exam.getAnswer(1),
															exam.getAnswer(2));				
					}
				}
			});
		 
			item.getChild("p").setTextElementListener(new TextElementListener()
			{
				String lang;
				
				@Override
				public void start(Attributes attributes) 
				{
					lang = attributes.getValue("lang");
				}

				@Override
				public void end(String body) 
				{
					exam.addQuestion(lang, body);
				}
			});
			
			item.getChild("r").setTextElementListener(new TextElementListener()
			{
				String correct;
				int i = 1;
				
				@Override
				public void start(Attributes attributes) 
				{
					correct = attributes.getValue("correct");
				}

				@Override
				public void end(String body) 
				{
					if (correct == null)
					{
						exam.addAnswer(i, body);
						i = i + 1;
						if (i == 3)
						{
							i = 1;
						}
					}
					else
					{
						exam.addAnswer(0, body);
					}
				}
			});
	        
			Xml.parse( _inputStream, Xml.Encoding.UTF_8, root.getContentHandler());
		}
		catch (Exception ex)
		{
		    Log.v("Exception", "Parsing error: " + TEST_FILE);
		    return false;
		}
		return true;
	}
}
