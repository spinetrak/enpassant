/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2018 spinetrak
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.spinetrak.enpassant.core.dsb.providers;

import net.spinetrak.enpassant.core.dsb.pojos.DSBPlayer;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Provider
@Produces(MediaType.TEXT_HTML)
public class DSBPlayersProvider implements MessageBodyWriter<List<DSBPlayer>> {

  @Override
  public boolean isWriteable(final Class<?> type_, final Type genericType_,
                             final Annotation[] annotations_, final MediaType mediaType_) {
    return type_ == ArrayList.class;
  }

  @Override
  public long getSize(final List<DSBPlayer> players_, final Class<?> type_, final Type genericType_,
                      final Annotation[] annotations_, final MediaType mediaType_) {
    // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
    return 0;
  }

  @Override
  public void writeTo(final List<DSBPlayer> players_, final Class<?> type_, final Type genericType_, final Annotation[] annotations_,
                      final MediaType mediaType_, final MultivaluedMap<String, Object> httpHeaders_,
                      final OutputStream out_) throws IOException, WebApplicationException {

    final Writer writer = new PrintWriter(out_);
    writer.write("<html>");
    writer.write("<body>");
    writer.write("<table>");
    writer.write("    <thead>\n" +
                   "    <tr>\n" +
                   "    <th>ZPS</th>\n" +
                   "    <th>DSB ID</th>\n" +
                   "    <th>FIDE ID</th>\n" +
                   "    <th>Name</th>\n" +
                   "    <th>Gender</th>\n" +
                   "    <th>YoB</th>\n" +
                   "    <th>DWZ</th>\n" +
                   "    <th>ELO</th>\n" +
                   "    <th>Status</th>\n" +
                   "    <th>Eligibility</th>\n" +
                   "    </tr></thead><tbody>");

    for(final DSBPlayer player : players_)
    {
      writer.write("<tr><td>"+player.getClubId()+"-"+player.getMemberId()+"</td>");
      writer.write("<td><a target='_blank' href='https://www.schachbund.de/spieler/"+player.getDsbId()+"'>"+player.getDsbId()+"</a></td>");
      writer.write("<td><a target='_blank' href='https://ratings.fide.com/card.phtml?event="+player.getFideId()+"'>"+player.getFideId()+"</a></td>");
      writer.write("<td>"+player.getName()+"</td>");
      writer.write("<td>"+player.getGender()+"</td>");
      writer.write("<td>"+player.getYoB()+"</td>");
      writer.write("<td>"+player.getCurrentDWZ()+"</td>");
      writer.write("<td>"+player.getCurrentELO()+"</td>");
      writer.write("<td>"+player.getStatus()+"</td>");
      writer.write("<td>"+player.getEligibility()+"</td></tr>");
    }

    writer.write("</tbody></table>");
    writer.write("</body>");
    writer.write("</html>");

    writer.flush();
    writer.close();
  }
}
